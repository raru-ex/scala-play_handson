<a id="markdown-目次" name="目次"></a>
# 目次

<!-- TOC -->

- [目次](#目次)
- [Lesson2 slickを利用したCRUDの実装](#lesson2-slickを利用したcrudの実装)
    - [一覧表示画面の修正](#一覧表示画面の修正)

<!-- /TOC -->

<a id="markdown-lesson2-slickを利用したcrudの実装" name="lesson2-slickを利用したcrudの実装"></a>
# Lesson2 slickを利用したCRUDの実装

この章ではArrayで実装されていたCRUDをDBを利用した形に修正していきます。  

<a id="markdown-一覧表示画面の修正" name="一覧表示画面の修正"></a>
## 一覧表示画面の修正

まず一覧画面から修正していきます。  
ここでは前章で作成したRepositoryを利用して一覧画面が表示できるようにしていきます。  

### Repositoryの説明

前章でRepositoryを作成しましたが、DBからデータを取得する`all()`メソッドがさらっと追加されていました。  
ここでは改めて`all()`メソッドについて説明していきます。  

以下が今回対象のコードです。  

`app/slick/repositories/TweetRepository.scala`
```scala
// ...省略
private val tweet = new TableQuery(tag => new TweetTable(tag))

// ########## [DBIO Methods] ##########

/**
  * tweetを全件取得
  */
def all(): Future[Seq[Tweet]] = db.run(tweet.result)
```

slickではTableQueryのインスタンスを利用してTableへアクセスを行います。  
細かい内部の動きは割愛しますがTweetTableのインスタンスを渡して作成されているため、Tweetテーブルに対しての処理が行えるようになっているわけですね。  
それを行っているのが以下です。  

```scala
private val tweet = new TableQuery(tag => new TweetTable(tag))
```

そしてここで生成されたインスタンスを利用して、実際に問合せを行うための処理が以下の`all()`メソッドです。  

```scala
def all(): Future[Seq[Tweet]] = db.run(tweet.result)
```

`tweet.result`の部分がqueryを組み立てている場所になります。  
今回は何もせずにテーブルのデータを全件取得しているというQueryになっています。  

これだとわかりづらいので、他にもサンプル実装を紹介してみます。  

```scala
// idが偶数のものだけ抽出
def allOdd(): Future[Seq[Tweet]] = db.run(
  tweet.filter(x => x.id % 2L === 0L).result
)

// idが一致するものを取得
def findById(id: Long): Future[Seq[Tweet]] = db.run(
  tweet.filter(x => x.id  === id).result
)
```

TableQueryのインスタンスであるtweet変数を起点にデータを絞り込んでいるのがわかりますね。  

最後に`db.run`の部分を説明します。  

`tweet.result`はまだQueryを組み立てだけの状態なので、これだけでは実際にDBへの問い合わせは行われません。  
実際にDBへの処理が行われるのは`db.run`が実行されたタイミングです。  

ここがplay-slickの場合には`HasDatabaseConfigProvider`に隠れている部分でもあります。  
このdbインスタンスは通常自分で作るのですが、play-slickの場合は上記のtraitの中で用意されています。  
通常は以下のように、自分で生成して利用します。  
```scala
val db = Database.forConfig("your_db_setting")
```

これで`def all()`の準備と理解ができました。  
次はControllerからこれを呼び出して一覧で表示できるようにしていきます。  

### Controllerの修正

Repositoryにデータ取得処理を追加できたので、それを呼び出す側のControllerを修正していきます。  
Controllerクラスも行数が大きくなっているので、修正した部分だけコードを載せていきますね。  

`app/controllers/tweet/TweetController.scala`
```scala
import slick.models.Tweet
import slick.repositories.TweetRepository

@Singleton
class TweetController @Inject()(
  val controllerComponents: ControllerComponents,
  tweetRepository:          TweetRepository // <= repositoryをDI
)(implicit ec: ExecutionContext)
extends BaseController
with I18nSupport {

// ... 省略 ...

  /**
    * Tweetを一覧表示
    *   Action.asyncとすることでreturnの型としてFuture[Result]を受け取れるように修正
    */
  def list() =  Action async { implicit request: Request[AnyContent] =>
    // DBから値を取得してreturnするように修正
    for {
      results <- tweetRepository.all()
    } yield {
      Ok(views.html.tweet.list(results))
    }
  }

// ... 省略 ...

}
```

修正箇所についてはコメントで補足していますが、改めて一つ一つ説明をしていきます。  
まずはクラス宣言の部分です。  

```scala
@Singleton
class TweetController @Inject()(
  val controllerComponents: ControllerComponents,
  tweetRepository:          TweetRepository // <= repositoryをDI
)(implicit ec: ExecutionContext)
```

ここではInjectの対象にrepositoryを追加しています。  
こうすることで実行時に`tweetRepository`にインスタンスを注入してくれるため、コントローラないでrepositoryを参照できるようになります。  

またRepositoryから受け取ったFutureを処理する必要があるので`(implicit ec: ExecutionContext)`を追加して、Futureに渡せるようにしています。  

次にlistアクションの処理をみてみます。  

```scala
def list() =  Action async { implicit request: Request[AnyContent] =>
  // DBから値を取得してreturnするように修正
  for {
    results <- tweetRepository.all()
  } yield {
    Ok(views.html.tweet.list(results))
  }
}
```

修正しているのは2ヶ所あり、一つが`Action async`の部分。  
もう一つが残りのfor式の部分ですね。  

forの書き方が慣れていないとわかりづらいかもしれませんね。  
scalaのfor式はmap/flatMapの糖衣構文になっており、今回のように1段の展開の場合には以下のコードと同じになります。  

```scala
tweetRepository.all().map(results => 
  Ok(views.html.tweet.list(results))
)
```

今回tweetRepositoryのreturnがFuture型になるので、for式のreturnがFuture[Result]型になっています。  
PlayではActionメソッドはreturnにResult型を要求しますが、これに対して`Action async`としてあげることでreturnの型要求をFuture[Result]にすることができます。  

そのためasyncのメソッドコールが追加されているのです。  
修正内容は比較的シンプルですね。  
利用しているモデルは変わっていないのでview側の修正は不要です。  

ここまで修正が終わったら、サーバを起動して動作を確認してみましょう。  
[http://localhost:9000/tweet/list](http://localhost:9000/tweet/list)  

以下のように５件のデータが表示できていればOKです。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/06_list_page.png" width="450">

#### [補足] ActionとAction asyncの違い

実装は完了していますが、少しActionについて補足します。  

先ほどの説明を見ると「Actionは逐次処理でAction asyncはFuture処理なんだ」と思うかもしれません。  
しかし実際にはActionとAction asyncに差はありません。  

これは[公式ドキュメント](https://www.playframework.com/documentation/2.8.x/ScalaAsync#Actions-are-asynchronous-by-default)にもNoteとして記載されています。  

```
Note: Both Action.apply and Action.async create Action objects that are handled internally in the same way.
There is a single kind of Action, which is asynchronous, and not two kinds (a synchronous one and an asynchronous one).
The .async builder is just a facility to simplify creating actions based on APIs that return a Future, which makes it easier to write non-blocking code.
```

意訳すると「どっちも同じで2種類あるわけではないよ」と書いてあります。  
言葉だけだと信じがたいので、実際にコードも見てみましょう。  

playのActionのapplyメソッドを確認してみると以下のようになっています。  

```scala
/**
 * Constructs an `Action` with default content.
 *
 * For example:
 * {{{
 * val echo = Action { request =>
 *   Ok("Got request [" + request + "]")
 * }
 * }}}
 *
 * @param block the action code
 * @return an action
 */
final def apply(block: R[B] => Result): Action[B] = async(block.andThen(Future.successful))
```

Actionの処理として書いていたblockをFutureに包んでasyncへ渡していますね。  
つまりどのように書いても結局asyncに渡されていくということです。  

Action, Action asyncの使い分けはbody内の処理がreturnする型が書きやすい方を使えばいいわけですね。  
このあたりが最初は慣れませんが、通常DBやAPIコールをすることが多いのでだいたいasyncに落ち着きますよ。  



## 詳細画面の修正

一覧画面ができたので、続いて詳細画面の修正を行っていきます。  

### Repositoryの修正

詳細ページを表示するために、対象データを1件取得する処理をRepositoryに追加していきましょう。

`app/slick/repositories/TweetRepository.scala`
```scala
/**
 * idを指定してTweetを取得
 */
def findById(id: Long): Future[Option[Tweet]] = db.run(
  tweet.filter(x => x.id  === id).result.headOption
)
```

sampleで作成していた処理と似ていますが、今回は主キーであるidでデータを取得するためOption型でデータを取得しています。  
filterはSQLでいうところの`where句`にあたります。  
単純にfilterを行うとデータがSeqとなるため、headOptionでOptionとして取得しているということです。  

慣れてしまうとScalaでmutableのArrayを扱うよりも、こちらの方が余程単純です。  


### Controllerの修正

一覧の時の修正を参考に、こちらも処理を修正していきましょう。  

`app/controllers/tweet/TweetController.scala`
```scala
  /**
    * 対象IDのTweet詳細を表示
    */
  def show(id: Long) = Action async { implicit request: Request[AnyContent] =>
    // idが存在して、値が一致する場合にfindが成立
    for {
      tweetOpt <- tweetRepository.findById(id)
    } yield {
      tweetOpt match {
        case Some(tweet) => Ok(views.html.tweet.show(tweet))
        case None        => NotFound(views.html.error.page404())
      }
    }
  }
```

今回もActionを`async`にしてfor式を利用してFutureを処理しています。  
単純な処理であればこの形式で処理できてしまうので見やすくなりますね。  

前回に引き続きViewに渡すモデルは変わっていないので、全体の修正は以上です。

早速詳細ページを表示して動作を確認してみましょう。  
[http://localhost:9000/tweet/1](http://localhost:9000/tweet/1)  

正常に画面が表示されればOKです。  

