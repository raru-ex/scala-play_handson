<a id="markdown-目次" name="目次"></a>
# 目次

<!-- TOC -->

- [目次](#目次)
- [Lesson2 Slickセットアップ](#lesson2-slickセットアップ)
    - [PlayframeworkにDB接続関連の設定を追加](#playframeworkにdb接続関連の設定を追加)
        - [build.sbtに依存関係を追加](#buildsbtに依存関係を追加)
        - [slick-evolutionsの設定](#slick-evolutionsの設定)
            - [DBへ接続するためにconfを設定](#dbへ接続するためにconfを設定)
                - [設定の補足](#設定の補足)
            - [Migration用のsqlを作成](#migration用のsqlを作成)
            - [evolutionsを実行](#evolutionsを実行)
        - [slick-codegenでslickのモデルを作成](#slick-codegenでslickのモデルを作成)
            - [sbt taskの作成](#sbt-taskの作成)
            - [SlickCodeGenの実行ファイルを作成する](#slickcodegenの実行ファイルを作成する)
                - [補足](#補足)
            - [SlickCodeGen Taskの実行](#slickcodegen-taskの実行)
        - [slick-codegenの日付型Mappingの変更](#slick-codegenの日付型mappingの変更)
            - [evolutionsのsqlに日付データを追加](#evolutionsのsqlに日付データを追加)
            - [SlickCodeGenのプログラム修正](#slickcodegenのプログラム修正)
                - [日付型の型を変更](#日付型の型を変更)
                - [おまけの部分](#おまけの部分)
            - [slick-codegenの実行と出力ファイル確認](#slick-codegenの実行と出力ファイル確認)
        - [play-slickを利用してモデルの操作を行う](#play-slickを利用してモデルの操作を行う)
    - [Tips](#tips)

<!-- /TOC -->

<a id="markdown-lesson2-slickセットアップ" name="lesson2-slickセットアップ"></a>
# Lesson2 Slickセットアップ

ハンズオンでDB接続を行うためにDB操作ライブラリのSlick関連の設定を行っていきます。  
一部複雑なものについては、おまけセクションで追加セットアップなどをおこなようにしていますが、おまけ部分は対応しなくても動作に支障はありません。  
あまり込み入ったことをしすぎるとハマることもあるので、おまけについては読み飛ばしていただいて問題ありません。  

<a id="markdown-playframeworkにdb接続関連の設定を追加" name="playframeworkにdb接続関連の設定を追加"></a>
## PlayframeworkにDB接続関連の設定を追加

<a id="markdown-buildsbtに依存関係を追加" name="buildsbtに依存関係を追加"></a>
### build.sbtに依存関係を追加

今回はplay-slickやslick-codegenを利用して環境を作成していきます。  

外部のライブラリを利用するには`build.sbt`の設定が必要です。  
Playでは`build.sbt`に依存関係を追加することで、対象のライブラリをダウンロードして利用できるようになります。  

本ハンズオンで利用するものとして、以下の依存関関係を`build.sbt`へ追加してください。  

```scala 
evolutions,
"com.typesafe.play"      %% "play-slick"            % "5.0.0",
"com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
// play-slickの5.0.0ではslick 3.3.2を利用しているため、codegenも同様に3.3.2を指定しています。
// https://github.com/playframework/play-slick#all-releases
"com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
"mysql"                   % "mysql-connector-java"  % "6.0.6",
```

依存関係を追加すると`build.sbt`ファイルは以下のようになります。  
`build.sbt`
```scala
name := """play-handson"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
  "com.typesafe.play"      %% "play-slick"            % "5.0.0",
  "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
  // play-slickの5.0.0ではslick 3.3.2を利用しているため、codegenも同様に3.3.2を指定しています。
  // https://github.com/playframework/play-slick#all-releases
  "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
  "mysql"                   % "mysql-connector-java"  % "6.0.6",
)
```

設定を追加したら一度この設定を読み込んでみましょう。  

```sh
$ cd {project_root}
$ docker-compose exec play-scala bash
/source# sbt update
```

今回追加したライブラリのダウンロードが始まれば設定はOKです。  

<a id="markdown-slick-evolutionsの設定" name="slick-evolutionsの設定"></a>
### slick-evolutionsの設定

早速ですが追加したライブラリを使っていきます。  

Slickのツールにはslick-evolutionsというDBマイグレーションツールとslick-codegenというDBからのモデル実装自動生成ツールがあります。  
ここではevolutionsを利用してDBをマイグレーションしていきます。  

evolutions以外ではFlywayというものも広く利用されています。  
もしかしたらFlywayのほうがよく使われているかもしれません。  

<a id="markdown-dbへ接続するためにconfを設定" name="dbへ接続するためにconfを設定"></a>
#### DBへ接続するためにconfを設定

slick-evolutionsは名前の通りSlickを経由してDBへの接続を行います。  
そのためまずはslickがDBへ接続できるように設定をしてあげる必要があります。  

playはデフォルトで`application.conf`の設定を読み込むようになっているので、このファイルに設定を追加していきましょう。  
今回はmysqlを利用するので以下のように設定を追加してください。  

`conf/application.conf`
```
slick.dbs {
  default {
    # mysqlと接続するためprofileにMySQLのものを指定
    profile = "slick.jdbc.MySQLProfile$"
    db {
      driver   = com.mysql.cj.jdbc.Driver,
      # dockerではコンテナ名を指定して通信可能なのでdbコンテナに3306ポート経由で通信
      url      = "jdbc:mysql://db:3306/twitter_clone?useSSL=false",
      # docker-composeで指定したものと合わせる
      user     = "root",
      password = "root",
    }
  }
}
```

<a id="markdown-設定の補足" name="設定の補足"></a>
##### 設定の補足

先ほど追加した設定を簡単に説明します。  

slickのデフォルトでの参照設定は`slick.dbs.{db_name}.db` というような構造になっており、`db_name`の部分は任意に設定可能です。  
ここで設定した`db_name`がこのシステム内での対象DBの名称となります。  
今回は慣習に倣い`default`としましたが`twitter_clone`や`master`, `slave`のようにすることも可能です。  

`url`の中で`db:3306`という箇所がありますが、この`db`はネットワーク上でのhost名になります。  
`docker-compose.yaml`でDB側のコンテナのコンテナ名を`db`としているので、そのコンテナ名で対象のサーバへアクセスをしています。

<a id="markdown-migration用のsqlを作成" name="migration用のsqlを作成"></a>
#### Migration用のsqlを作成

DB接続の設定ができたので、次はDBに対して実行するSQLを用意します。  
evolutionsではデフォルトで`conf/evolutions/{db_name}/{連番}.sql`というファイルを検索しにいくので、以下のような構造でフォルダ/ファイルを作成してください。  

```sh
conf
└── evolutions
    └── default
        └── 1.sql
```

1.sqlは一旦以下のようにシンプルな形で記載して、動作を確認していきましょう。  

```sql
-- Tweet schema

-- !Ups
CREATE TABLE tweet (
    id      bigint(20)    NOT NULL AUTO_INCREMENT,
    content varchar(120)  NOT NULL,
    -- created_at timestamp NOT NULL,
    -- updated_at timestamp NOT NULL
    PRIMARY KEY (id)
);

-- !Downs
DROP TABLE tweet;
```

slickで日付周りの設定をするのが少し面倒なので、ここでは一旦timestampの設定をコメントアウトしています。  
最終的には設定していくのでご安心ください。  

<a id="markdown-evolutionsを実行" name="evolutionsを実行"></a>
#### evolutionsを実行

準備が完了したので、実際にマイグレーションを実行してみます。  
evolutionsはブラウザからマイグレーションを実行する作りになっているため、コンテナへアクセスし、サーバを起動してください。

```sh
$ docker-compose exec play-scala bash
/source# sbt run
```

サーバが起動できたらブラウザからPlayへ[アクセス](http://localhost:9000)します。

そうすると以下の画面が表示されると思います。  
  
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/02_evolutions_execute.png" width="450">

この画面の`Apply this script now!`ボタンから、evolutionsの実行を許可してマイグレーションを走らせることができます。  
実際にボタンを押して実行をしてみたら、mysql側のコンテナへアクセスして動作を確認してみましょう。  

```sh
$ docker-compose exec db bash
/# mysql -u root -proot
mysql> use twitter_clone
mysql> show tables; 
# +-------------------------+
# | Tables_in_twitter_clone |
# +-------------------------+
# | play_evolutions         |
# | tweet                   |
# +-------------------------+
# 2 rows in set (0.01 sec)
```

このように`tweet`テーブルが作成されていたら、マイグレーションは成功です。  
一緒に作成されている`play_evolutions`テーブルは、evolutions側でマイグレーションの実行状況を管理するためのテーブルになります。  
特に気にしなくて大丈夫です。  

<a id="markdown-slick-codegenでslickのモデルを作成" name="slick-codegenでslickのモデルを作成"></a>
### slick-codegenでslickのモデルを作成

slick-codegenというのはDBのtable情報からslickで利用するモデルを自動生成してくれるライブラリです。
codegeはコンパイル時の自動実行とsbt commandに登録しての手動実行など、いくつかの実行方法があります。  
今回はsbt taskとして登録して手動実行できるように設定してきますが、sbtについては詳しく知らないため、設定方法のみ記述し詳細については割愛させていただきます。  

<a id="markdown-sbt-taskの作成" name="sbt-taskの作成"></a>
#### sbt taskの作成

まずは以下のコードを`build.sbt`へ追加します。  

```scala
// add code generation task
lazy val slickCodeGen = taskKey[Unit]("execute Slick CodeGen")
slickCodeGen         := (runMain in Compile).toTask(" com.example.SlickCodeGen").value
```

1行目で`slickCodeGen`という名前でコマンド(Task)のインスタンスを作成しています。  
2行目ではそのタスク名に対して、特定のクラス処理を登録するようなことをしています。※ 詳細は理解できていません。
sbtでは`:=`を演算子を利用してKeyに対しての実態を定義していきます。  

ここでは`com.example.SlickCodeGen`というクラスを登録しています。  
そのためこれから、この名前に一致するクラスを作成していきます。

また`toTask()`に渡すときに、先頭にスペースを追加していますが、これがないと正常にファイルの呼び出しが行えませんので注意してください。理由は理解していません。  

<a id="markdown-slickcodegenの実行ファイルを作成する" name="slickcodegenの実行ファイルを作成する"></a>
#### SlickCodeGenの実行ファイルを作成する

それでは早速SlickCodeGenの実行ファイルを作成していきます。  
今回は以下のファイルの追加/変更を行っていきます。  

- app/tasks/SlickCodeGen.scala
- build.sbt
- conf/application.conf

`app/tasks/SlickCodeGen.scala`
```scala
// Taskに登録したものと同様にpackageを指定
package com.example

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator

object SlickCodeGen extends App {
  // typesafe configを利用してapplication.confをロード
  val config      = ConfigFactory.load()
  val defaultPath = "slick.dbs.default"

  // 末尾の$を削除
  // s補間子つき文字列では${}. $hogeで変数を参照可能です。この場合 slick.dbs.default.profileのように展開されます。
  val profile   = config.getString(s"$defaultPath.profile").dropRight(1)
  val driver    = config.getString(s"$defaultPath.db.driver")
  val url       = config.getString(s"$defaultPath.db.url")
  val user      = config.getString(s"$defaultPath.db.user")
  val password  = config.getString(s"$defaultPath.db.password")

  // pathが別なので直接呼び出し
  val outputDir = config.getString("slick.codegen.outputDir")
  val pkg       = config.getString("application.package")

  // slick-codegenを実行
  SourceCodeGenerator.main(
    Array(profile, driver, url, outputDir, pkg, user, password)
  )
}
```

ここで`TypesafeConfig`というライブラリを利用しています。  
これは外部のライブラリになるため`build.sbt`へ依存関係を追加します。  

`build.sbt`
```scala
"com.typesafe" % "config" % "1.4.0"
```

依存関係を追加したら、`SlickCodeGen`クラスで利用している設定情報を`application.conf`へ追記していきます。

```
slick {
  # slick.dbsをslickとdbsに分離しているので注意
  dbs {
    default {
      profile = "slick.jdbc.MySQLProfile$"

      db {
        driver   = com.mysql.cj.jdbc.Driver,
        url      = "jdbc:mysql://db:3306/twitter_clone?useSSL=false",
        user     = "root",
        password = "root",
      }
    }
  }
  codegen {
    # ここでの.はroot directoryとなる
    outputDir = "./output/codegen"
  }
}

# DB関係なくシステム全体での設定のためslickの外に定義しています
application {
  package = "com.example"
}
```

slick部分の構造が少し変更されているので気をつけてください。  

<a id="markdown-補足" name="補足"></a>
##### TypesafeConfig導入の補足

通常のplayでの実装ではControllerへのDIからconfigを利用するため、直接ロードするのはあまり御行儀が良いものではないのですが、バッチプログラムになるのでControllerを経由できないことや、そんなにテストするようなコードでもないので直接取り出すことを選択しています。  

<a id="markdown-slickcodegen-taskの実行" name="slickcodegen-taskの実行"></a>
#### SlickCodeGen Taskの実行

ファイルの修正ができたら、早速コマンドの実行をしてみましょう。  

```sh
$ docker-compose exec play-scala bash
/source# sbt slickCodeGen
```

コマンドの実行に成功すると以下のように`Tables.scala`ファイルが作成されます。  

```sh
output
└── codegen
    └── com
        └── example
            └── Tables.scala
```

今回はevolutionsのテーブルも対象に取られているため、かなり`うわっ...`となるファイルになっていると思いますが、Tweet部分に限れば以下のようになっています。   

`output/codegen/com/example/Tables.scala`
```scala
  implicit def GetResultTweetRow(implicit e0: GR[Long], e1: GR[String]): GR[TweetRow] = GR{
    prs => import prs._
    TweetRow.tupled((<<[Long], <<[String]))
  }

  class Tweet(_tableTag: Tag) extends profile.api.Table[TweetRow](_tableTag, Some("twitter_clone"), "tweet") {
    def * = (id, content) <> (TweetRow.tupled, TweetRow.unapply)
    def ? = ((Rep.Some(id), Rep.Some(content))).shaped.<>({r=>import r._; _1.map(_=> TweetRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content: Rep[String] = column[String]("content", O.Length(120,varying=true))
  }

  lazy val Tweet = new TableQuery(tag => new Tweet(tag))
```

`Slick`は詳細を理解しようとすると大変なのでここでは詳細は省きますが、これでSlickからTweetテーブルを操作するために必要なコードが用意できました。  
このファイルを自力で実装するのはミスも発生して大変なので、特に慣れないうちはcodegenから生成するのが良いと思います。  

<a id="markdown-slick-codegenの日付型mappingの変更" name="slick-codegenの日付型mappingの変更"></a>
### slick-codegenの日付型Mappingの変更

slick-codegenではTimestampやDatetimeを`java.sql.Timestamp`にMappingしています。  
このままだと使いづらいので`java.time.LocalDateTime`でモデル生成が行われるようにcodegenの設定を変更していきます。  

<a id="markdown-evolutionsのsqlに日付データを追加" name="evolutionsのsqlに日付データを追加"></a>
#### evolutionsのsqlに日付データを追加

まずは`conf/evolutions/default/1.sql`を修正してきます。  
日付関連のデータを持っていなかったので、よくある日付型カラムを追加していきましょう。  

```sql
-- Tweet schema

-- !Ups
CREATE TABLE tweet (
    id         BIGINT(20)    NOT NULL AUTO_INCREMENT,
    content    VARCHAR(120)  NOT NULL,
    posted_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- !Downs
DROP TABLE tweet;
```

ここで変更したものは、改めて`sbt run`で起動したサーバにアクセスを行えば、最初と同じようにブラウザから実行が可能です。  

<a id="markdown-slickcodegenのプログラム修正" name="slickcodegenのプログラム修正"></a>
#### SlickCodeGenのプログラム修正

今度はSQLに合わせて、SlickCodeGen関連のファイルを修正していきます。  
今回修正するファイルは以下

- [Rename]: app/tasks/SlickCodeGen.scala -> app/tasks/CustomSlickCodeGen.scala
- build.sbt

`app/tasks/CustomSlickCodeGen.scala`
```scala
package com.example

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api.Database
import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

// object名は変更
object CustomSlickCodeGen extends App {
  // typesafe configを利用してapplication.confをロード
  val config      = ConfigFactory.load()
  val defaultPath = "slick.dbs.default"

  // 末尾の$を削除
  val profile   = config.getString(s"$defaultPath.profile").dropRight(1)
  val driver    = config.getString(s"$defaultPath.db.driver")
  val url       = config.getString(s"$defaultPath.db.url")
  val user      = config.getString(s"$defaultPath.db.user")
  val password  = config.getString(s"$defaultPath.db.password")

  // pathが別なので直接呼び出し
  val outputDir = config.getString("slick.codegen.outputDir")
  val pkg       = config.getString("application.package")

  // db接続用のインスタンスを生成
  val db  = Database.forURL(
    url      = this.url,
    driver   = this.driver,
    user     = this.user,
    password = this.password
  )

  // evolutions用のテーブルを対象から外す
  val ignoreTables        = Seq("play_evolutions")
  val codegenTargetTables = MySQLProfile.createModel(Some(
    MySQLProfile.defaultTables.map(
      _.filter(table => !ignoreTables.contains(table.name.name.toLowerCase))
    )
  ))

  // モデルを生成したい対象を渡す
  val modelFuture = db.run(codegenTargetTables)

  // CustomCodeGeneratorを生成しつつ、writeToFileで書き出す
  val codegenFuture = modelFuture.map(model => new SourceCodeGenerator(model) {
    // LocalDateTimeのimportを追加
    override def code = "import java.time.{LocalDateTime}" + "\n" + super.code

    // override table generator
    override def Table = new Table(_){
      // disable entity class generation for tables with more than 22 columns
      override def hugeClassEnabled = false

      override def Column = new Column(_){
        // datetimeはデファオルトでjava.sql.Timestamp型になるので、LocalDateTimeに書き換え
        override def rawType = model.tpe match {
          case "java.sql.Timestamp" => "LocalDateTime"
          case _                    =>
            super.rawType
        }
      }
    }
  }.writeToFile(profile, outputDir, pkg))

  // 処理が完了するまで待つ
  Await.result(codegenFuture, Duration.Inf)
}

```

```scala
// -- build.sbt: slickCodeGenコマンドに紐づけるクラスの変更
slickCodeGen         := (runMain in Compile).toTask(" com.example.CustomSlickCodeGen").value
```

今回の修正はやや複雑になっていますね。  
いろいろと実装が入っていますが、注目すべきポイントは1点とおまけ1つです。

<a id="markdown-日付型の型を変更" name="日付型の型を変更"></a>
##### Codegenで自動生成される日付型の型を変更

本セクションでの目的である日付型の変換をしている部分はここです。  

```scala
override def Column = new Column(_){
  // datetimeはデファルトでjava.sql.Timestamp型になるので、LocalDateTimeに書き換え
  override def rawType = model.tpe match {
    case "java.sql.Timestamp" => "LocalDateTime"
    case _                    =>
      super.rawType
  }
}
```

ここでColumn定義の実装をoverrideして差し替えています。  
今回はmodelの型をcaseでチェックして`java.sql.Timestamp`のときに`LocalDateTime`になるように変更をかけていますね。  
  
文字列で指定していますが、結局Codegenでは`.scala`の拡張子を持つテキストファイルを出力しているだけなので、最終成果物であるテキスト上で出力されて欲しい文字列に置き換えてあげれば良いです。  
  
ちなみに、このままではimportが不明確で実際にプログラムから利用するときにはLocalDateTimeが見つけられません。  
そのため、その少し前の部分で以下のようにしてimport文を追加しています。  

```scala
// LocalDateTimeのimportを追加
override def code = "import java.time.{LocalDateTime}" + "\n" + super.code
```

これは最終的な出力コードの先頭にimportを追加しているような動きになります。  

<a id="markdown-おまけの部分" name="おまけの部分"></a>
##### おまけの部分

これはおまけなので、説明は省略しますが以下の部分でevolutions用のテーブルをモデル作成対象から外しています。  

```scala
// evolutions用のテーブルを対象から外す
val ignoreTables        = Seq("play_evolutions")
val codegenTargetTables = MySQLProfile.createModel(Some(
  MySQLProfile.defaultTables.map(
    _.filter(table => !ignoreTables.contains(table.name.name.toLowerCase))
  )
))
```

これでevolutionsが対象から外れて、少し見やすくなりましたね。  

<a id="markdown-slick-codegenの実行と出力ファイル確認" name="slick-codegenの実行と出力ファイル確認"></a>
#### slick-codegenの実行と出力ファイル確認

設定ができたので、改めてslick-codegenを実行してみましょう。  

```sh
# 今回は今までと違う形で実行。
$ docker-compose exec play-scala sbt slickCodeGen
```

実行後に出力されたファイルの一部がこちら

```scala
// ... 省略
import java.time.{LocalDateTime}
// ... 省略
/** Database column posted_at SqlType(DATETIME) */
val postedAt: Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
/** Database column created_at SqlType(DATETIME) */
val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
/** Database column updated_at SqlType(DATETIME) */
val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
```

ちゃんとLocalDateTimeになっていますね。  
長くなってしまいましたが、これでslick-codegen側の設定は一旦完了になります。

### WIP

Slick3.3以降はMySQLを利用するとDateTime等の日付系を文字列で取得するようになっている。  
その後LocalDateTimeなど型に合わせて変換をしようとするが、そのときにデフォルトだとISO_LOCAL_DATE_TIMEを利用してしまう。  
この結果`yyyy-MM-dd HH:mm:ss`などの形式はparseエラーになってしまう。`yyyy-MM-ddTHH:mm:ss`なら通る  

対応策

* 文字列のまま受け取って、モデルとのマッピングでモデルを生成するところで日付型の部分をparseする
* 上記の処理をimplicitでslick column mappingで対応する
* MySQLProfileを拡張して独自のprofileを作成する (試したけどうまく動かなかった)

それぞれのパターンの実装をサンプルで記載する

<a id="markdown-play-slickを利用してモデルの操作を行う" name="play-slickを利用してモデルの操作を行う"></a>
### play-slickを利用してモデルの操作を行う

<a id="markdown-tips" name="tips"></a>
## Tips

- db名変更をした場合などは`docker/db/mysql_data/*`を削除してからコンテナの再起動をする
