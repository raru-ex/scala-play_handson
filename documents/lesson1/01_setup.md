<a id="markdown-目次" name="目次"></a>
# 目次

<!-- TOC -->

- [目次](#目次)
- [Lesson1](#lesson1)
- [基本情報](#基本情報)
    - [sbtとは](#sbtとは)
    - [Playframeworkとは](#playframeworkとは)
        - [他のScala WEB Framework](#他のscala-web-framework)
    - [オススメの開発エディタ](#オススメの開発エディタ)
- [ハンズオン](#ハンズオン)
    - [注意事項](#注意事項)
    - [推奨環境](#推奨環境)
    - [Playの導入](#playの導入)
    - [統合開発環境(IntelliJ IDEA)のセットアップ](#統合開発環境intellij-ideaのセットアップ)
        - [IDEAのダウンロード](#ideaのダウンロード)
        - [IDEAへのプロジェクトの取り込み](#ideaへのプロジェクトの取り込み)
    - [一覧ページ作成](#一覧ページ作成)
        - [ルーティング](#ルーティング)
        - [Controllerの作成](#controllerの作成)
        - [画面の作成](#画面の作成)
        - [モデルの作成とリスト表示](#モデルの作成とリスト表示)
            - [モデルの作成](#モデルの作成)
            - [モデルをViewへ渡す](#モデルをviewへ渡す)
            - [Viewでモデルを受け取り表示](#viewでモデルを受け取り表示)
    - [詳細ページ作成](#詳細ページ作成)
        - [ルーティングの作成](#ルーティングの作成)
        - [アクションとViewの追加](#アクションとviewの追加)
        - [一覧からのリンク作成](#一覧からのリンク作成)
        - [エラーページ作成](#エラーページ作成)
    - [登録ページの作成](#登録ページの作成)
        - [ルーティングの追加](#ルーティングの追加)
        - [登録用画面の実装](#登録用画面の実装)
            - [Formの追加](#formの追加)
            - [画面表示用アクションの追加](#画面表示用アクションの追加)
            - [viewの作成](#viewの作成)
        - [登録処理の実装](#登録処理の実装)
            - [tweetsのSeqをmutable化](#tweetsのseqをmutable化)
            - [登録用アクションの実装](#登録用アクションの実装)
                - [バリデーションエラーの場合](#バリデーションエラーの場合)
                - [登録成功の場合](#登録成功の場合)
        - [表示・テンプレートの調整](#表示・テンプレートの調整)
            - [日本語メッセージの表示](#日本語メッセージの表示)
                - [application.confの設定](#applicationconfの設定)
                - [messagesファイルの設定](#messagesファイルの設定)
            - [入力のヒント表示制御](#入力のヒント表示制御)
    - [更新ページの作成](#更新ページの作成)
    - [Twirlの共通コンポーネント作成](#twirlの共通コンポーネント作成)
    - [おまけ](#おまけ)
        - [CustomErrorHandlerの作成](#customerrorhandlerの作成)
            - [CustomErrorHandlerクラスの作成](#customerrorhandlerクラスの作成)
            - [利用するエラーハンドラをPlayに設定](#利用するエラーハンドラをplayに設定)

<!-- /TOC -->

<a id="markdown-lesson1" name="lesson1"></a>
# Lesson1

Lesson1ではPlayを利用して、簡単なCRUDを作成していきます。  

まずはPlayframeworkをシンプルな状態のまま利用して、Playframeworkに慣れていきましょう。  
  
またScala-Playで開発を行うにあたり、必要となる基本的な知識・言葉についても簡単にではありますが補足させていただきます。  
  
既にご存知の内容であればスキップしていただいて問題ございません。  

<a id="markdown-基本情報" name="基本情報"></a>
# 基本情報

<a id="markdown-sbtとは" name="sbtとは"></a>
## sbtとは

主にScalaで利用されているビルドツールです。  
JavaでいうところのAnt, Mavenにあたります。  
設定ファイルの記述はScalaのDSLを用いて行います。  

ライブラリの依存関係の解決や、デプロイのためのパッケージング、インクリメンタルなテストやコンパイルの実行などができます。  
この後利用しますが、他にもシードからのプロジェクト生成などを行うことなども可能です。  

scalacコマンドなどを利用してscalaプログラムの実行を行うこともできますが、一般的にはsbtを利用してその上でScalaを利用することが多いと思います。  

参照: [Wikipedia](https://ja.wikipedia.org/wiki/Sbt)

<a id="markdown-playframeworkとは" name="playframeworkとは"></a>
## Playframeworkとは

元々はJava向けのWebフレームワーク。  
2010年11月にリリースされたPlay 1.1からはScalaをサポートしており、現在広く利用されているフレームワークの一つです。  

MVCアーキテクチャに親和性が高く、Web Applicationに必要な一通りの機能が網羅されています。  
ScalaでのWeb開発では利用率が高く、参考情報も豊富なため今回のハンズオンに採用しています。  
ちなみにPlay2.3のころから後述のAkkaを統合しています。  

[参照]  
[Play Framework](https://ja.wikipedia.org/wiki/Play_Framework)  
[JetBrains: どのフレームワーク / ライブラリをウェブ開発に定期的に使用していますか？](https://www.jetbrains.com/ja-jp/lp/devecosystem-2019/scala/)  

<a id="markdown-他のscala-web-framework" name="他のscala-web-framework"></a>
### 他のScala WEB Framework

その他の主要なフレームワークについても一部ご紹介しておきます。  

- [Scalatra](https://scalatra.org/)
  - takezoeさんの作成しているGitbucketで利用されているフレームワークです
  - RubyのフレームワークであるSinatraに影響を受けています
- [Skinny Framework](http://skinny-framework.org/)
  - seratchさんの作成してるフレームワークです
  - Ruby on Railsにインスパイアされ、作成されています
- [Akka](https://en.wikipedia.org/wiki/Akka_(toolkit))
  - Playframeworkの内部でも利用されているライブラリ
  - 公式ではオープソースツールキットおよびランタイムと記載されており、フレームワークではない
  - Sprayの後継として位置付けられていて、Akka○○という形でいくつかのライブラリに分かれている
  - Akka httpが一番よく聞く。Http関連のモジュール
  - Actor modelを採用している -> [Wikipedia](https://ja.wikipedia.org/wiki/%E3%82%A2%E3%82%AF%E3%82%BF%E3%83%BC%E3%83%A2%E3%83%87%E3%83%AB)


<a id="markdown-オススメの開発エディタ" name="オススメの開発エディタ"></a>
## オススメの開発エディタ

- IntelliJ IDEA
- Visual Studio Code & metals
- Vim or Emacs & metals

正直なところ、IDEについてはほぼほぼIntellij IDEAで統一されている印象です。  
Community EditionでもScala Pluginを導入すれば、個人で開発する範囲であればあまり不自由なく開発が行えると思います。  

他の選択肢として、最近はMetalsというScala向けのLanguage Serverの開発が進んでおり、LSPを利用した連携機能を持つエディタであればかなり快適に開発を行うことも可能です。  
事実は私は個人ではVimとMetalsを利用して開発を行っています。  

個人的には、強いこだわりがない限りは`IntelliJ IDEA`の利用をオススメします。  


<a id="markdown-ハンズオン" name="ハンズオン"></a>
# ハンズオン

本ハンズオンでは現在(2020年02月)時点で最新である、Playの2.8を利用してハンズオンを進めていきます。  

<a id="markdown-注意事項" name="注意事項"></a>
## 注意事項

本ハンズオンは以下を前提として構築されています。  
前提となっている環境と差異がある方は、各々読みかえていただいたり各環境ごとに適切にセットアップを行ってください。  

1. マシンにsbtがインストールされている
2. Mac OSのパソコンを利用している

[sbt インストール方法 Windowsの場合](https://www.scala-sbt.org/1.x/docs/ja/Installing-sbt-on-Windows.html)  
[sbt インストール方法 Macの場合](https://www.scala-sbt.org/1.x/docs/ja/Installing-sbt-on-Mac.html)  
[sbt インストール方法 Linuxの場合](https://www.scala-sbt.org/1.x/docs/ja/Installing-sbt-on-Linux.html)

<a id="markdown-推奨環境" name="推奨環境"></a>
## 推奨環境

 ハンズオンが作成されている環境がsbt1.3系を利用しているため、これを推奨します。  

- 1.3系のsbtが導入されている

<a id="markdown-playの導入" name="playの導入"></a>
## Playの導入

sbtのコマンドからPlayのシードプロジェクトを作成していきます。  
任意のプロジェクトディレクトリを作成して、以下のコマンドを実行してください。  

あくまで例になりますがMacでの作業をイメージして作成していきます。  

```sh
$ cd ~/Documents/workspace/
$ sbt new playframework/play-scala-seed.g8
# 参照: https://www.playframework.com/documentation/2.8.x/NewApplication
# ... 省略

This template generates a Play Scala project

name [play-scala-seed]: play-handson
organization [com.example]: com.example
```

name部分でプロジェクト名を指定しています。  
organizationは個人のドメインをお持ちの方は、その逆順で記載しても良いです。  
今回はサンプルのため`com.example`を指定しています。  

これでコマンドを実行したフォルダ以下に`name`で指定したプロジェクト名のフォルダが作成されます。  

```sh
$ tree  -L 2 | pbcopy
.
└── play-handson
    ├── app
    ├── build.sbt
    ├── conf
    ├── project
    ├── public
    ├── target
    └── test
```

`sbt new`が完了したら、ローカルでサーバを起動して動作を確認してみましょう。  

```sh
$ cd {your_project_root}
$ sbt run

# .... 以下の表示がでたらサーバが起動しています。
# --- (Running the application, auto-reloading is enabled) ---
# 
# [info] p.c.s.AkkaHttpServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000
# 
# (Server started, use Enter to stop and go back to the console...)
```

サーバが起動したら、ブラウザからアクセスをしてみましょう。  
[http://localhost:9000](http://localhost:9000)

以下の画面が表示されていればOKです。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/01_play_hello_world.png" width="450">

導入自体はこれで完了になります。  
Scalaは環境も整備されてきており、誰でも比較的簡単に始めることができる環境が整ってきているのではないでしょうか。  

<a id="markdown-統合開発環境intellij-ideaのセットアップ" name="統合開発環境intellij-ideaのセットアップ"></a>
## 統合開発環境(IntelliJ IDEA)のセットアップ

今回はIntelliJ IDEAの無償版を利用したセットアップの手順を記載していきます。  
Vimについては自己責任で出来る人しかいないと思うので、割愛します。  
Visual Studio Codeについては、個人的にmetalsのフルパワーがみてみたいので命尽きる前のいつの日にか手順を作ってみたいと思っております。  


<a id="markdown-ideaのダウンロード" name="ideaのダウンロード"></a>
### IDEAのダウンロード

[こちらのサイト](https://www.jetbrains.com/ja-jp/idea/)からIDEAのダウンロードリンクを押下してください。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/02_JetBrains_top.png" width="450">

各々OSに合わせたダウンロードページへ遷移します。(たぶん)  
ダウンロードページに遷移したら、以下のボタンから無償版のIDEAをダウンロードします。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/03_JetBrains_download_page.png" width="450">

IDEAをインストールできたら、IDEAを起動して次のステップへ進んでください。  

<a id="markdown-ideaへのプロジェクトの取り込み" name="ideaへのプロジェクトの取り込み"></a>
### IDEAへのプロジェクトの取り込み

まず、IDEAを起動したら`Open`からプロジェクトを開いていきましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/04_IntelliJ_top.png" width="450">

ファインダーから選択するときには、対象のプロジェクトを反転させてから`Open`で問題ありません。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/05_chose_project.png" width="450">

次にIDEAのテーマを選択します。  
ここはお好みのテーマを選択してください。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/06_select_view_mode.png" width="450">

次のステップでは`shell`からIDEAを起動するためにPATHを通すか選択する画面になります。  
ここもご自身で自由に設定いただいて問題ありません。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/07_select_bin_setting.png" width="450">

次は好みの設定があるようでしたら変更いただいても問題ありませんが、基本的にはそのままで大丈夫です。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/08_setting1.png" width="450">

この画面では印がしていあるように、Scalaのインストールだけは必須で行ってください。  
他のものについては任意です。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/09_install_scala.png" width="450">

これでIDEAが起動して、プロジェクトが読み込まれます。  
またそのときに下の画像のようなポップアップが表示されたら`Import Changes`を選択しましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/10_import_sbt.png" width="450">

sbtに変更が加わったときなどに表示されます。これをしないとsbt関連の変更が適用されないのでご注意ください。  
  
`Enable Auto-Import`をすると自動で設定を更新するようになりますが、sbtの読み込みはマシンパワーを使うことがあるので私は手動が好きです。  
ただこれはお好みで選んでいただいて構いません。  

では、起動が完了したら試しに`app/controllers/HomeController.scala`のファイルを開いてみましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/11_open_homecontroller.png" width="450">

ソースコード上で、21行目の`Action`に`Cmd`キーを押しながらカーソルを移動してみてください。  
マウスが当たったときにカーソルが指マークに変わるとおもいます。  
そこでクリックをしてみましょう。  
Actionの実装に飛ぶことができれば設定は正常にされている状態になっています。  

うまく動作しないときはJDKのインストールとScalaの設定ができていないときがあります。  
そのときは声をかけてください。  

これでIDEAの設定は完了になります。  

<a id="markdown-一覧ページ作成" name="一覧ページ作成"></a>
## 一覧ページ作成

それではPlayの機能を触りながら、簡単に一覧ページから作成していきましょう。  
今回はPlayに集中するためDBへのアクセスは行わずに作成してみようと思います。  

<a id="markdown-ルーティング" name="ルーティング"></a>
### ルーティング

まずはリクエストに対する処理の設定をみていきます。  
Playでは`conf/routes`のファイルでルーティングを管理しています。  
今の状態の`conf/routes`ファイルを開いてみると以下のようになっていると思います。  

```
# ... 一部抜粋
# An example controller showing a sample home page
GET     /                           controllers.HomeController.index
```

これは`GET`Methodの`/`へのリクエストに対して`controllers.HomeController.index`を設定すると言うことになります。  
この設定のおかげで、Playは`http://localhost:9000`、つまりルート(`/`)に対して`HomeController`の`index`アクションを実行するということを理解できるようになります。  

今回は一覧表示機能を作成したいので、以下のようにルーティングを設定してみましょう。  

```
GET     /tweet/list                 controllers.tweet.TweetController.list
```
今回は意図的に`tweet`というパッケージを間に挟んでいます。  
その場合にどうなるのか、スッとイメージができたりできなかったりすることがあるので参考として追加しているため特別な意味はありません。  

次は設定したルーティングに必要なコントローラーを作成してきましょう。   

<a id="markdown-controllerの作成" name="controllerの作成"></a>
### Controllerの作成

Playでは基本的にはControllerでリクエストに対しての処理を受け取ります。  
Controller内にあるリクエストに対するメソッドは一般的に`Action`と呼ばれます。(他のプログラミング言語、フレームワークでも同様です)

ここでは`TweetController.scala`を作成して、その中で`list`アクションを作成していきます。  

`app/controller/tweet/TweetController.scala`
```scala
package controllers.tweet

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent

/**
  * @SingletonでPlayFrameworkの管理下でSingletonオブジェクトとして本クラスを扱う指定をする
  * @Injectでconstructorの引数をDIする
  * BaseControllerにはprotected の controllerComponentsが存在するため、そこに代入されている。
  * controllerComponentsがActionメソッドを持つため、Actionがコールできる
  *   ActionはcontrollerComponents.actionBuilderと同じ
  */
@Singleton
class TweetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def list() =  Action { implicit request: Request[AnyContent] =>
    // Ok()はステータスコードが200な、Resultをreturnします。
    // つまり正常系としてviews.html.tweet.listのコンテンツを返すということになります。
    Ok(views.html.tweet.list())
  }
}
```

これで`http://localhost:9000/list`のリクエストに対してのアクションを作成できました。  
ただまだこれではエラーが出ていると思います。  
それはこのリクエストに対するレスポンスに指定している`views.html.tweet.list`のファイルが存在しないからです。  

次は一覧画面のためのhtmlを作成していきましょう。  

<a id="markdown-画面の作成" name="画面の作成"></a>
### 画面の作成

Playではデフォルトでは`Twirl`というテンプレートエンジンを利用して画面を作成します。  
Twirlのファイルは`views/`直下に配置されており、拡張子が`.scala.html`となっています。  
今回は`views/tweet/list.scala.html`を作成していきます。  

このフォルダ構成が先ほどの`Ok(views.html.tweet.list())`の指定とマッピングされています。  
以下のようなイメージですね。  
`views.html`   => `views/`  
`tweet.list()` => `tweet/list.scala.html`  

では、ファイルを作成して、まずは以下のように中身を実装していきましょう。  

```html
@* これはTwirlのコメントです。
以下はview templeteでの引数を受け取る記載です。
今回は引数が不要のため @() となっています。
*@
@()

@main("一覧画面") {
  <h1>一覧画面です</h1>
}
```

@mainの部分については後ほど説明をしますので、今は「h1の表示を出すんだな」くらいの理解で問題ありません。  

ここまで出来たら、一度ページへアクセスして動作を確認してみましょう。 
以下のような画面が表示されればOKです。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/12_list_page_part1.png" width="450">


<a id="markdown-モデルの作成とリスト表示" name="モデルの作成とリスト表示"></a>
### モデルの作成とリスト表示

単純なページ表示は行えたので、次はモデルを作成してそのモデルを一覧表示してみます。  

<a id="markdown-モデルの作成" name="モデルの作成"></a>
#### モデルの作成

今回利用する`app/models/Tweet.scala`を作成してきましょう。  

```scala
package models

// case classについての説明は省略
// 参考: https://docs.scala-lang.org/ja/tour/case-classes.html
case class Tweet(
  id:      Option[Long],
  content: String
)
```

今のところは非常にシンプルなクラスになりました。  
`case class`についての説明は省略しますが、非常に雑に説明すると`toString`, `equals`がいい感じに実装されていたり`apply`や`unapply`メソッドなどが実装されている便利なClassです。  

<a id="markdown-モデルをviewへ渡す" name="モデルをviewへ渡す"></a>
#### モデルをViewへ渡す

先ほど作成したモデルをコントローラからViewへ渡してみましょう。  

`app/controllers/tweet/TweetController.scala`
```scala
// ... 省略
def list() =  Action { implicit request: Request[AnyContent] =>
  // 1から10までのTweetクラスのインタンスを作成しています。
  // 1 to 10だとIntになってしまうの1L to 10LでLongにしています。
  val tweets: Seq[Tweet] = (1L until 10).map(i => Tweet(Some(i), s"test tweet${i.toString}"))

  // viewの引数としてtweetsを渡します。
  Ok(views.html.tweet.list(tweets))
}
```

今回はDBを利用しないので、Controller側に決め打ちで実装しています。  
`views.html.tweet.list()`に引数としてtweetsを渡すところまではできました。  
次はView側で引き渡されたTweetを受け取って表示をしてみます。  

<a id="markdown-viewでモデルを受け取り表示" name="viewでモデルを受け取り表示"></a>
#### Viewでモデルを受け取り表示

早速コードを修正していきましょう。  

`views/tweet/list.scala.html`
```html
@* Twirl側でもクラスを正しく認識するためにscalaファイルと同様にimportが必要です。  *@
@import models.Tweet

@*
以下はview templeteでの引数を受け取る記載です。
今回はTweetの一覧を取得するため@(tweets: Seq[Tweet]) となっています。
*@
@(tweets: Seq[Tweet])

@main("一覧画面") {
  <h1>一覧画面です</h1>
  <ul>
    @* Twirlでのfor記法です。forと(の間にスペースを入れると動かないので注意してください *1 *@
    @for(tweet <- tweets) {
    <li>@tweet.content</li>
    }
  </ul>
}
```

説明についてはプログラム上のコメントに記載してありますが、新しくimportと引数の受け取り、受け取ったインスタンの出力を追加してあります。  
Twirlの記法については[こちら](https://www.playframework.com/documentation/ja/2.3.x/ScalaTemplates)を参考にしてください。  

*1:  
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/13_twirl_for_space_error.png" width="450">

これで一覧表示の実装は完了です。  
ブラウザから動作を確認してみましょう。  
[http://localhost:9000/tweet/list](http://localhost:9000/tweet/list)

以下のように表示されていればOKです。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/14_list_view_part1.png" width="450">


<a id="markdown-詳細ページ作成" name="詳細ページ作成"></a>
## 詳細ページ作成

一覧が作成できたので、次は詳細ページを作成していきます。  
データがコンテンツくらいしかないので寂しいページにはなりますが、復習もかねて一覧ページを作成したのと同じように実装を進めていきます。  

<a id="markdown-ルーティングの作成" name="ルーティングの作成"></a>
### ルーティングの作成

詳細ページはどのデータの詳細情報を表示するか判断するための情報が必要になります。  
今回はモデルにあるIDでデータを特定するように実装を進めます。  
以下のようにroutesファイルにルーティングを追加してください。  

`conf/routes`
```
GET     /                           controllers.HomeController.index
GET     /tweet/list                 controllers.tweet.TweetController.list
# 追加
GET     /tweet/:id                  controllers.tweet.TweetController.show(id: Long)
```

`/tweet/:id`という記載がでてきました。  
ここでは`:id`箇所のデータをパラメータとして受け取っています。  
例えば`http://localhost:9000/tweet/1`のとき`TweetController`の`show`メソッドに`1`が引数として渡されます。  

またパラメータ受け取りは今回のようなURL文字列からの取得以外にも、通常通りのQueryStringからの取得も可能です。  

例えば以下のようなroutesがあったとします。  
```
GET     /tweet/detail               controllers.tweet.TweetController.show(id: Long)
```
このとき`http://localhost:9000/tweet/detail?id=1`のようなURLであれば、showに1が渡されます。 

<a id="markdown-アクションとviewの追加" name="アクションとviewの追加"></a>
### アクションとViewの追加

routesに追加ができたので、そこに紐づくアクションとViewを追加してきます。  

`app/controllers/tweet/TweetController.scala`
```scala
@Singleton
class TweetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  // DBのMockとして利用したいので、クラスのフィールドとして定義し直す
  val tweets: Seq[Tweet] = (1L to 10L).map(i => Tweet(Some(i), s"test tweet${i.toString}"))

  def list() =  ... 省略 ...

  def show(id: Long) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.tweet.show(
      // tweetsの一覧からIDが一致するものを一つ取得して返す
      // getは良くない書き方なため、後のセクションで修正する
      tweets.find(_.id.get == id).get
    ))
  }
}

```

まずtweetsをフィールドとして定義し直しています。  
これはDBなしでデータを一定期間保持しておくための実装なのです。  

showメソッドの中でOption型を直接getしていますが、これはnullに対して安全な処理ができるメリットを捨ててしまうことになるため、後ほど修正していきます。  

`views/tweet/show.scala.html`
```html
@import models.Tweet
@(tweet: Tweet)

@main("詳細画面") {
  <h1>詳細画面です</h1>
  <div id="detail">
    <div>id: @tweet.id</div>
    <div>id: @tweet.content</div>
  </div>
}
```

TwirlについてはSeqだった引数がTweetになっているくらいの変化しかありません。  

ここまでできたら、以下のURLにアクセスして画面が正常に表示できるか確認してみましょう。  
[http://localhost:9000/tweet/1](http://localhost:9000/tweet/1)

以下のように表示されていればOKです。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/15_detail_view_part1.png" width="450">

<a id="markdown-一覧からのリンク作成" name="一覧からのリンク作成"></a>
### 一覧からのリンク作成

詳細ページ自体は完成したので、次は先きほど作成した一覧ページからリンクを通してみます。  

`views/tweet/list.scala.html`
```html
@import models.Tweet
@(tweets: Seq[Tweet])

@main("一覧画面") {
  <h1>一覧画面です</h1>
  <ul>
    @for(tweet <- tweets) {
    <li>
      @* よくウェブ上で @routes.HomeController.index のようなものをみるがこれはtwirlではデフォルトでcontrollers.routesがインポートされているためcontroller部分が省略されているだけ。*@
      <a href="@controllers.tweet.routes.TweetController.show(tweet.id.getOrElse(0))">@tweet.content</a>
    </li>
    }
  </ul>
}
```

href部分ではroutesファイルの設定から、紐づくURLを作成するようにしてあります。  
書き方は`{Controllerのパッケージ}.routes.{Controller名}`となります。
  
コメントにも記載していますが、ウェブ上で良くみる`@routes`から始まる書き方は実は自動的に`controllers.routes`がインポートされているためにcontrollersを省略された状態になっています。  

ここを理解していないと独自でパッケージを切ったりしていく時に、非常に苦労することになるので頭の隅に残しておきましょう。  

では、最後に動作確認です。  
[http://localhost:9000/tweet/list](http://localhost:9000/tweet/list)

以下のようにリンクが表示され、リンククリックで詳細ページが表示されたら完了です。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/16_list_view_part2.png" width="450">

<a id="markdown-エラーページ作成" name="エラーページ作成"></a>
### エラーページ作成

先ほど省略したエラーページの表示を行います。  
まずは実装からみてみましょう。  

`app/controllers/tweet/TweetController.scala`
```scala
def show(id: Long) = Action { implicit request: Request[AnyContent] =>
    // idが存在して、値が一致する場合にfindが成立
    tweets.find(_.id.exists(_ == id)) match {
      case Some(tweet) => Ok(views.html.tweet.show(tweet))
      // status codeを404にしつつページを返しています。
      case None        => NotFound(views.html.error.page404())
   }
  }
```

この実装では元々`get`をしてしまっていた部分についても、改善するようにしています。  
tweetのidがURLから受け取ったidと一致するものを`find`しています。  
`exists`はNone.existsの場合に常に`false`になります。  

今回はデータのない場合の表示なのでstatusとして404(NotFound)を指定しています。  
Ok, NotFoundは同じクラスなので同様の使い方が可能です。  

次にNotFoundで指定してるページを作成します。  

`views/error/page404.scala.html`
```html
@()

@main("ページが見つかりません") {
  <h1>ページが見つかりません。</h1>
}
```

ここまでできたら動作を確認してみましょう。  
この状態で存在しないTweetを参照しようとすると以下のようになります。  
[http://localhost:9000/tweet/1111](http://localhost:9000/tweet/1111)

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/17_simple_404.png" width="450">

ステータスコードが404で、作成したページが表示されていることが確認できますね。  
エラーページを作成する方法は以上です。  

<a id="markdown-登録ページの作成" name="登録ページの作成"></a>
## 登録ページの作成

次は登録機能を作成していきます。  
例によってconf, controllerと修正していきましょう。  

登録処理は今までの機能とは違い、画面からformの値を受け取るという動作があります。  
Formはよく利用する機能なので登録・更新と処理を書く中で少しずつ慣れていきましょう。  

<a id="markdown-ルーティングの追加" name="ルーティングの追加"></a>
### ルーティングの追加

`conf/routes`
```
GET     /                           controllers.HomeController.index
GET     /tweet/list                 controllers.tweet.TweetController.list
GET     /tweet/:id                  controllers.tweet.TweetController.show(id: Long)
# 下の2つを追加
GET     /tweet/store                controllers.tweet.TweetController.register
# POST    /tweet/store                controllers.tweet.TweetController.store
```

今回は`/tweet/store`というルーティングをget, postのそれぞれで追加しています。  
これは登録用画面の表示と、実際に登録処理を行うアクションで2つのアクションが必要になるためです。  

またルーティングは追加してみましたが、実はこのルーティングは正常に動作しません。  
理由は`/tweet/:id`の設定の方が上位に書かれているからです。  
playのルーティングは先勝ちになっているようで`/tweet/store`にアクセスしようとすると`:id`の部分に`store`が取られてしまいます。  

これを回避するには2つの方法があります。  

1. `/tweet/:id`が数値のみをとるように変更する
2. storeのルーティングを上にあげる

基本的には1の方が良いので、ここでは1の方法で修正してみます。

`conf/routes`
```
GET     /tweet/{<[0-9]+>id}         controllers.tweet.TweetController.show(id: Long)
```

これで`show`のルーティングでは0-9の数字しか受け付けなくなりました。  

<a id="markdown-登録用画面の実装" name="登録用画面の実装"></a>
### 登録用画面の実装

続いてコントローラを修正していきますが、今回はアクションの追加のみではなくFormオブジェクトの設定も行なっていきます。  

<a id="markdown-formの追加" name="formの追加"></a>
#### Formの追加

まずFormオブジェクトの追加を行っていきます。  
Formオブジェクトを利用することでPOSTでの値受け取りをフレームワーク側に移譲しつつ、バリデーションなどの処理を簡単に適用することができます。  

習うよりコードを見た方が早いと思うので、早速コードをみてみましょう。  
Formはいくつかの書き方が出来るので複数の書き方を記載していますが、結論パターン2の書き方で実装を進めていきます。。  

```scala
// パターン2用のcase class
case class TweetFormData(content: String)

class TweetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  // ...省略

  // パターン1: 既存クラスを使いまわして、apply, unapplyを自前で書くパターン
  val form1: From[Tweet] = Form(
    // html formのnameがcontentのものを140文字以下の必須文字列に設定する
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )
    // apply, unapplyを自分で書いているパターン
    ((content: String)  => Tweet(None, content))
    ((v: Tweet)         => Some(v.content))
  )

  // パターン2: Form用にcase classを作成するパターン(推奨)
  val form2: From[Tweet] = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetFormData.apply)(TweetFormData.unapply)
  )


  // パターン3: tuple, singleを利用するパターン
  // 受けとるデータが単数なのでsingleとしていますが、複数の場合にはtuple()になります。 
  val form3 = Form(
    single(
      "content" -> nonEmptyText(maxLength = 140)
    )
  )


  // ...省略
}
```

`single`, `tuple`と`mapping`の使い分けは、Formから受け取った値をクラスにマッピングしたいときには`mapping`  
そのまま利用したいときには`single`, `tuple`を利用する、なります。  

デフォルトで利用できるバリデータは他にも`email`, `number`, `boolean`などがあります。  
Formの使い方の詳細は以下の公式ドキュメントを参照してください。  
[参照: Form submission and validation](https://www.playframework.com/documentation/2.8.x/ScalaForms)

今回利用するフォームがオブジェクトができたので、次はルーティングに対応するアクションを追加していきましょう。  

<a id="markdown-画面表示用アクションの追加" name="画面表示用アクションの追加"></a>
#### 画面表示用アクションの追加

まずは簡単な登録画面表示のアクションから作成していきます。  

`app/controllers/tweet/TweetController.scala`
```scala
// controllersクラスの外に記載
case class TweetFormData(content: String)

// ...省略
val form = Form(
    // html formのnameがcontentのものを140文字以下の必須文字列に設定する
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetFormData.apply)(TweetFormData.unapply)
  )

// ...省略
def register() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.tweet.store(form))
}

// コンパイルエラー回避用に何もしない登録用のstoreメソッドも作成
def store() = Action { implicit request: Request[AnyContent] =>
  NoContent
}
```

シンプルですね。  
ここで先ほど作成したformを画面へ渡しています。  
先ほどの実装ではフォームをいくつか作成していましたが、ここでは`form2`のみを残して`form`にリネームしています。  

<a id="markdown-viewの作成" name="viewの作成"></a>
#### viewの作成

アクションが作成できたのでViewを追加します。  
今までのViewを参考にしつつ、以下のようにファイルを作成していきましょう。  

`views/tweet/store.scala.html`
```html
@import controllers.tweet.TweetFormData
@(form: Form[TweetFormData])

@main("登録画面") {
  <h1>登録画面です</h1>
  @helper.form(action = controllers.tweet.routes.TweetController.store()) {
    @helper.inputText(form("content"))
    <input type="submit" value="登録">
  }
}
```

今回新しく`@helper`というパッケージを利用しています。  
ここにはFormを利用するためのヘルパー関数がいくつも用意されています。  
importに`@import helper._`を追加して利用するのも、わりと一般的です。  

ここまで出来たら、一度コンパイルしてみましょう。  
そうすると以下のようにエラーになると思います。  
```sh
$ sbt compile

An implicit MessagesProvider instance was not found.  Please see https://www.playframework.com/documentation/latest/ScalaForms#Passing-MessagesProvider-to-Form-Helpers
[error]     @helper.inputText(form("content"))
[error]                      ^
[error] one error found
[error] (Compile / compileIncremental) Compilation failed
[error] Total time: 0 s, completed 2020/02/24 21:37:20
```

これはinputTextが暗黙の引数としてmessagesProviderのインスタンスを必要としているために発生します。  
何をヒントに修正していけばいいのかは、エラーメッセージの中に書かれていますね。  
[Passing-MessagesProvider-to-Form-Helpers](https://www.playframework.com/documentation/latest/ScalaForms#Passing-MessagesProvider-to-Form-Helpers)

implicitが出てくると非常に難しく感じてしまいますが、最初は直接手で引数を渡すと面倒くさいから自動で渡すようにしている、くらいの理解で良いと思います。  

エラー修正のため、messagesProviderをviewへ渡していきます。  

```html
@import controllers.tweet.TweetFormData
@* 以下の引数ブロックにimplicit用の引数を追加 *@
@(form: Form[TweetFormData])(implicit messageProvider: MessagesProvider)

@main("登録画面") {
  <h1>登録画面です</h1>
  @helper.form(action = controllers.tweet.routes.TweetController.store()) {
    @helper.inputText(form("content"))
    <input type="submit" value="登録">
  }
}
```

この状態でもう一度コンパイルをしてみると、どうでしょう。

```sh
$ sbt compile

An implicit MessagesProvider instance was not found.  Please see https://www.playframework.com/documentation/latest/ScalaForms#Passing-MessagesProvider-to-Form-Helpers
[error]     Ok(views.html.tweet.store(form))
[error]                              ^
[error] one error found
[error] (Compile / compileIncremental) Compilation failed

```

先ほどと同様のエラーですが、エラーが出る箇所がコントローラまで上ってきています。  
そのため、次はコントローラを修正してあげる必要があります。  
修正方法はエラーメッセージの中にある`Please see`のリンク先を見ればわかるようになっています。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/19_implicit_MessagesProvider.png" width="450">

こちらを参考にコントローラを直してみましょう。  

`app/controllers/tweet/TweetController.scala`
```scala
class TweetController @Inject()(val controllerComponents: ControllerComponents) 
extends BaseController with I18nSupport {
```

`with`句で新しくI18nSupportをmixinしています。  
これでコンパイルをするとエラーが解決されているのが確認できるはずです。  

```sh
$ sbt compile
[success] Total time: 0 s, completed 2020/02/24 22:09:40
```

ここまで出来たら、一度登録画面を表示してみましょう。  
[http://localhost:9000/tweet/store](http://localhost:9000/tweet/store)

以下のように画面が表示されていればOKです。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/20_view_register_page.png" width="450">

ちょっと不格好ですが、この辺りは後ほど修正していきます。  
少し長丁場になっていますが、次は登録処理を作成していきましょう。  

<a id="markdown-登録処理の実装" name="登録処理の実装"></a>
### 登録処理の実装

登録処理の実装は今までのアクションと比べて少し複雑になります。  
具体的には画面から受け取ったフォームデータの利用や、入力ミスがあった場合の元画面でのエラー表示などがあります。  

<a id="markdown-tweetsのseqをmutable化" name="tweetsのseqをmutable化"></a>
#### tweetsのSeqをmutable化

tweetのインスタンスデータを保持しつつ、可変な状態にするために`tweets`フィールドを可変配列に置き換えていきます。  
Scalaではimmutableなオブジェクトやリストを利用するのが基本のため、あくまでサンプルのための実装になります。  

`app/controllers/tweet/TweetController.scala`
```scala
class TweetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  // DBのMockとして利用したいので、mutableなクラスのフィールドとして定義し直す
  val tweets = scala.collection.mutable.ArrayBuffer((1L to 10L).map(i => Tweet(Some(i), s"test tweet${i.toString}")): _*)

// ...省略


  def list() =  Action { implicit request: Request[AnyContent] =>
    // 型エラー回避のためtweets.toSeqでimmutableSeqに変換してから画面に渡す
    Ok(views.html.tweet.list(tweets.toSeq))
  }
```

これでtweetsをデータ保持しつつ可変な配列に変更できました。  

<a id="markdown-登録用アクションの実装" name="登録用アクションの実装"></a>
#### 登録用アクションの実装

tweetsを可変Seqに変更できたので、改めて登録処理を実装していきます。  
作成が完了した処理をみてみましょう。  

```scala
def store() = Action { implicit request: Request[AnyContent] =>
  // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
  form.bindFromRequest().fold(
    // 処理が失敗した場合に呼び出される関数
    // 処理失敗の例: バリデーションエラー
    (formWithErrors: Form[TweetFormData]) => {
      BadRequest(views.html.tweet.store(formWithErrors))
    },
    // 処理が成功した場合に呼び出される関数
    (tweetFormData: TweetFormData) => {
      // 登録処理としてSeqに画面から受け取ったコンテンツを持つTweetを追加
      tweets += Tweet(Some(tweets.size + 1L), tweetFormData.content)
      // 登録が完了したら一覧画面へリダイレクトする
      Redirect("/tweet/list")
      // 以下のような書き方も可能です。twirl側と同じですね
      // Redirect(controllers.tweet.routes.TweetController.list())
    }
  )
}
```

`bindFromRequest`はimplicitでrequestを受け取っています。  
なので、このリクエスト情報からformで設定したマッピング情報を元に入力チェックと値変換を行います。  
その処理の成否によって`fold`で処理を分岐しているという動きです。  

失敗時には400のBadRequestとして受け取ったフォームデータにエラーメッセージを追加して元の画面に戻しています。  
成功時には受け取ったデータから新しいTweetを作成して一覧画面へリダイレクトしています。  

ちなみに、`fold()()`で失敗を左、成功を右のような動きは`Option`や`Either`にも似たようなものがあります。  
`Seq`だとまた雰囲気の違う動きになるのですが、この辺の違いは圏論でいうところの`Catamorphism`というものを理解するとわかるようになるみたいです。  
私はこの辺りは良くわからないので省略しますが、このfoldの使い方は割とよくあるみたいなので気が向いた際に学習してみたり、頭の隅に置いておくとコードが読みやすくなるかもしれません。  

では、処理が書けたの実際に登録画面から登録してみてください。
[http://localhost:9000/tweet/store](http://localhost:9000/tweet/store)

登録してみるとどのようになるでしょうか。  
以下のような画面になっていないでしょうか。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/21_csrf_error.png" width="450">

これだけだとよくわからないですよね。  
こういうときはコンソールに出ているメッセージを確認してみましょう。  

```sh
[warn] p.filters.CSRF - [CSRF] Check failed because no or invalid token found in body for /tweet/store
[warn] p.filters.CSRF - [CSRF] Check failed with NoTokenInBody for /tweet/store
```

サーバのログにこのようなメッセージが出ていました。  
CSRFのチェックが正常に行われずにエラーになっているようです。  

実はPlayではPOST, PUTなどはデフォルトでCSRFチェックがかかるようになっています。  
そのため画面からトークンを渡しておらずエラーになるということですね。  
詳細は[こちら](https://www.playframework.com/documentation/2.8.x/ScalaCsrf)に記載されています。  

では、公式サイトの情報に習って修正を行っていきましょう。  

```html
@import controllers.tweet.TweetFormData
@* CSRFトークンの生成ヘルパーで、requestHeaderを必要としているのでこちらも暗黙パラメートして渡しています。 *@
@(form: Form[TweetFormData])(implicit messagesProvider: MessagesProvider, requestHeader: RequestHeader)

@main("登録画面") {
  <h1>登録画面です</h1>
  @helper.form(action = controllers.tweet.routes.TweetController.store()) {
    @* CSRFトークンの生成ヘルパーを呼び出している。これでいい感じにトークンが用意されます。 *@
    @helper.CSRF.formField
    @helper.inputText(form("content"))
    <input type="submit" value="登録">
  }
}
```

今回implicitの引数を一つ追加しています。  
implicitと書かれていませんが、implicitにした引数のブロックは全部implicitになりますし、それ以外は定義できません。  
以下のようなことをするとコンパイルエラーになります。  
`(messagesProvider: MessagesProvider, implicit requestHeader: RequestHeader)`

もう一つこのrequestHeaderを利用して、CSRFトークンを生成するヘルパーを呼び出しています。  
実際に実装の定義を見ていると以下のようになっており、implicitで引数を求めていますね。  
`def formField(implicit request: RequestHeader): Html`

それでは今度こそ動作をみてみましょう。  
[http://localhost:9000/tweet/store](http://localhost:9000/tweet/store)

<a id="markdown-バリデーションエラーの場合" name="バリデーションエラーの場合"></a>
##### バリデーションエラーの場合

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/22_validation_error.png" width="450">

<a id="markdown-登録成功の場合" name="登録成功の場合"></a>
##### 登録成功の場合

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/23_store_success.png" width="450">

それぞれこのようになっていれば完了です。  

<a id="markdown-表示・テンプレートの調整" name="表示・テンプレートの調整"></a>
### 表示・テンプレートの調整

基本的にはこれまでの部分で登録処理は完成ですが、英語でメッセージが出ていたり、フォームヒントが出ていることが見栄え的に良くないので、その部分の対応の仕方を記載します。  

<a id="markdown-日本語メッセージの表示" name="日本語メッセージの表示"></a>
#### 日本語メッセージの表示

まずエラーメッセージやフォームへの注釈表示を日本語に対応してみます。  

多言語化対応はi18nの機能で実装されており、それぞれの言語に合わせたメッセージを設定することで霧狩ることができます。  

今回は例として日本語への対応を行ってみます。  

<a id="markdown-applicationconfの設定" name="applicationconfの設定"></a>
##### application.confの設定

まずはどの言語に対応するかを`application.conf`へ設定していきます。  

`conf/application.conf`
```
# application.confでは#の行がコメントになります。  

# i18n設定
# conf/{play.i18n.path}messagesとなる
play.i18n.path         = "messages/"

# HTTP HeaderのAccept-Languageの値と比較を行い許可する対象を設定する
# messages.{langs}のファイルが読み込み対象になる
play.i18n.langs        = ["ja", "en-US"]
```

今回設定しているのは2つ。  
1つ目がメッセージファイルの配置場所です。  
デフォルトでは`conf/`直下がファイルの配置場所になっていますが、各言語のファイルが並ぶと見辛いので場所を変更しています。  
通常日本語くらいしか利用しないとは思いますが、フォルダ位置を変更したくなる人もいるとおもうため、そこでハマってしまう人を減らす意図も有ります。  

2つ目が対応するAccept-Languageの値の指定です。  
今回は日本語とアメリカ英語を対象にしています。  

このようにするとAccept-Languageヘッダに`ja`、`en-US`の文字があったときに優先度に合わせてplayが自動的に読み込みに行く`message`ファイルを切り替えてくれます。  

以下の部分にあるものです。  
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/26_accept-languages.png" width="450">

このとき対象のファイルは`messages.{lang}`というフォーマットになります。  
今回だと`messages.ja`と`messages.en-US`になります。  

またどの設定にも当てはまらない場合にはデフォルトファイルとして`messages`ファイルを読みに行くようになっています。  

一点注意が必要で、全てのクライアントが適切にAccept-Languageを指定してくれるとは限らないということです。  
どのように管理するかは自由ですが、上記忘れずにおかないと人によってはメッセージが違う、という不具合に繋がってしまいます。

<a id="markdown-messagesファイルの設定" name="messagesファイルの設定"></a>
##### messagesファイルの設定

application.confの設定が終わったので、messagesファイルを作成します。  
今回はen-USは省略して日本語とデフォルトだけ作成してみます。  

まずはファイルを設定する前の状態で、表示をしてみましょう。  
[http://localhost:9000/tweet/store](http://localhost:9000/tweet/store)  
入力なしで登録しようとすると以下のように表示されると思います。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/24_default_message.png" width="450">

この未設定状態のメッセージが設定ファイルに記載するときのkey名になっているので、覚えておいてください。  
動きが確認できたら、改めてmessagesファイルの設定を行っていきましょう。  

`conf/messages/message`
`conf/messages/message.ja`
```
# https://www.playframework.com/documentation/latest/ScalaI18N
# 上記リンクに用意されているメッセージ一覧や利用方法が記載されています。
error.invalid=入力が不正です
error.required=入力は必須です
error.maxLength={0}文字以内で入力してください

# 未設定状態では、このkey名が画面に表示されるのでそれをみて設定をすれば良いです。
constraint.required=*
constraint.maxLength=最大{0}文字まで
```

これで今回必要な分の設定は完了です。  
ブラウザからメッセージを確認してみてください。  
[http://localhost:9000/tweet/store](http://localhost:9000/tweet/store)  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/25_ja_message.png" width="450">

これでmessagesの設定は完了です。  

ちなみにこの設定ファイルでのkey名ですが、これはリンク先のページもに一覧で記載されていますので、目を通していただけるとどんなものがあるのかわかると思います。  

<a id="markdown-入力のヒント表示制御" name="入力のヒント表示制御"></a>
#### 入力のヒント表示制御

続いてFormHelperの制御を行っていきます。  
今の実装だとconstraintsが全て表示されてしまって見辛いので、これを非表示にしてみます。  
設定できる値は以下を参照ください。  
[公式ドキュメント](https://www.playframework.com/documentation/ja/2.4.x/ScalaCustomFieldConstructors)

`app/views/tweet/store.scala.html`
```html
@import controllers.tweet.TweetFormData
@(form: Form[TweetFormData])(implicit messagesProvider: MessagesProvider, requestHeader: RequestHeader)

@main("登録画面") {
  <h1>登録画面です</h1>
  @helper.form(action = controllers.tweet.routes.TweetController.store()) {
    @helper.CSRF.formField
    @* contentはフリー入力系の項目なのでtextareaに変更しています。*@
    @helper.textarea(form("content"),
      @* 通常のhtml属性を設定。'を先頭につけて -> で値を渡します。 *@
      'rows -> 7, 'cols -> 40,
      @* helperに渡す属性です。 'をつけて->で値を渡すのは同様です。 *@
      '_label -> "ツイート" ,'_showConstraints -> false
    )
    <input type="submit" value="登録">
  }
}
```

Tweetのcontentはinput textにするには文字数が多すぎるのでtextareに変更しました。  
またtextareaのサイズ調整にrows, colsのhtmlのtextareタグにある属性を利用しています。  
基本的にform helperへの値渡しは(symbol, value)の形式で渡します。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/27_symbol_tuple.png" width="450">

'_label, '_showConstraintsはhelper側で用意されている属性です。  
今回はこの2つを設定してみました。  
では、この状態で動きを見てみましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/28_store_html_no_constraints.png" width="450">

このようにhtmlの属性と、helperの動きを制御できました。  

登録を画面を通しての基本的はtwirl, formの利用方法はこれで完了です。  
次はおさらいも兼ねて変更画面を作成してきましょう。

<a id="markdown-更新ページの作成" name="更新ページの作成"></a>
## 更新ページの作成

更新ページの作成は今まで作ってきたものを参考に進めていけば、基本的には問題なく作成できます。  
早速それぞれ必要なファイルを作成してみましょう。  



<a id="markdown-twirlの共通コンポーネント作成" name="twirlの共通コンポーネント作成"></a>
## Twirlの共通コンポーネント作成

<a id="markdown-おまけ" name="おまけ"></a>
## おまけ

<a id="markdown-customerrorhandlerの作成" name="customerrorhandlerの作成"></a>
### CustomErrorHandlerの作成

システム開発ではよくエラーハンドラーを作成したくなることがあるので、作成の仕方を記載します。  
公式サイトに記載されている内容とほとんど同じではありますが、もう少し知りたい方は[こちら](https://www.playframework.com/documentation/2.8.x/ScalaErrorHandling)を確認ください。  

<a id="markdown-customerrorhandlerクラスの作成" name="customerrorhandlerクラスの作成"></a>
#### CustomErrorHandlerクラスの作成

さっそく今回利用する`CustomErrorHandler`クラスを作成していきます。  
今回のサンプルではPlayがデフォルトで表示する404ページを、以前のセクションで作成した404ページに差し替えてみます。  

```scala
package http

import javax.inject._
import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._

@Singleton
class CustomErrorHandler @Inject() (
  env:          Environment,
  config:       Configuration,
  sourceMapper: OptionalSourceMapper,
  router:       Provider[Router]
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(
      NotFound(views.html.error.page404())
    )
  }
}
```

非常にシンプルですね。  
今回は404ページを差し替えたいので`onNotFound`を`override`しています。  

ちなみにですが`DefaultHttpErrorHandler`の実装はこのようになっています。  
```scala
protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful {
      if (config.showDevErrors) {
        NotFound(views.html.defaultpages.devNotFound(request.method, request.uri, router)(request))
      } else {
        NotFound(views.html.defaultpages.notFound(request.method, request.uri)(request))
      }
    }
  }
```

こちらは環境設定により、エラー画面を出し分けているようですね。  
開発を行う際にこちらの方が都合が良いようでしたら、この実装を参考にしてください。  


他にも`onClientError`, `onServerError`, `onForbidden`, `onBadRequest`などが存在しますが、同じように`override`が可能です。  
詳しくは`play.api.http`パッケージの`HttpErrorHandler.scala`あたりをみてみましょう。  

Playでは良い感じにそれぞれのメソッドを呼び出してくれるので、対応するメソッドを上書きしてあげれば良いと言う作りです。  


<a id="markdown-利用するエラーハンドラをplayに設定" name="利用するエラーハンドラをplayに設定"></a>
#### 利用するエラーハンドラをPlayに設定

クラスが作成できたらPlayにこのクラスを利用することを伝えてあげましょう。  
Playではエラーハンドラを指定する方法が2つあります。

1. プロジェクトrootにErrorHandler.scalaを配置する
2. application.confに設定する

今回は2の方法で対応してみたいと思います。  

`conf/application.conf`
```
play.http.errorHandler = "http.CustomErrorHandler"
```

これで設定は完了です。  
それでは動作をみてみましょう。  
[http://localhost:9000/hogehoge/fugafuga](http://localhost:9000/hogehoge/fugafuga)

アクセすると以下の画面になっていれば実装完了です。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/18_on_notfound.png" width="450">
