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
                - [TypesafeConfig導入の補足](#typesafeconfig導入の補足)
            - [SlickCodeGen Taskの実行](#slickcodegen-taskの実行)
        - [slick-codegenの日付型Mappingの変更](#slick-codegenの日付型mappingの変更)
            - [evolutionsのsqlに日付データを追加](#evolutionsのsqlに日付データを追加)
            - [SlickCodeGenのプログラム修正](#slickcodegenのプログラム修正)
                - [Codegenで自動生成される日付型の型を変更](#codegenで自動生成される日付型の型を変更)
                - [おまけの部分](#おまけの部分)
            - [slick-codegenの実行と出力ファイル確認](#slick-codegenの実行と出力ファイル確認)
    - [slickのモデルを実装](#slickのモデルを実装)
        - [Slick3.3とMySQLを組み合わせた場合の日付型対応](#slick33とmysqlを組み合わせた場合の日付型対応)
            - [日付関連のRDBごと比較](#日付関連のrdbごと比較)
        - [未対応の場合のエラーと原因](#未対応の場合のエラーと原因)
        - [LocalDateTimeのparseエラー対応方針決め](#localdatetimeのparseエラー対応方針決め)
        - [独自Profile実装](#独自profile実装)
        - [play-slickを利用してモデルの操作を行う](#play-slickを利用してモデルの操作を行う)
            - [そもそもplay-slickとは？](#そもそもplay-slickとは)
            - [play-slickのコード上の機能](#play-slickのコード上の機能)
            - [Tweet関連のDBアクセス用クラスを作成する](#tweet関連のdbアクセス用クラスを作成する)
            - [独自実装したprofileを利用するように設定する](#独自実装したprofileを利用するように設定する)
    - [おまけ](#おまけ)
        - [MappedColumnTypeを利用したマッピング](#mappedcolumntypeを利用したマッピング)
        - [Stringのまま受け取って個別にマッピング](#stringのまま受け取って個別にマッピング)

<!-- /TOC -->

<a id="markdown-lesson2-slickセットアップ" name="lesson2-slickセットアップ"></a>
# Lesson2 Slickセットアップ

ハンズオンでDB接続を行うためにDB操作ライブラリのSlick関連の設定を行っていきます。  
一部複雑な箇所もあるのでハンズオンとして対応せずに、この実装がインクルードされた状態のシードを利用して実装を進めていただいても大丈夫です。  
その場合にも一応何をしているのかを把握していただいた方が良いとは思うので、サッと目を通してもらえると幸いです。  

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
// 指定すべきバージョンは以下のリンク先
// https://scala-slick.org/doc/3.3.1/database.html
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
  // 指定すべきバージョンは以下のリンク先
  // https://scala-slick.org/doc/3.3.1/database.html
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

ここでは`com.example.SlickCodeGen`というクラスを登録していますね。  
そのためこれから、この名前に一致するクラスを作成していきます。  

また`toTask()`に渡すときに、先頭にスペースを追加していますが、これがないと正常にファイルの呼び出しが行えませんので注意してください。  

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

<a id="markdown-typesafeconfig導入の補足" name="typesafeconfig導入の補足"></a>
##### TypesafeConfig導入の補足

通常のplayではconfigはControllerへのDIから利用するため、直接ロードするのはあまり御行儀が良いものではないのですが、バッチプログラムでControllerを経由できないことや、そんなにテストするようなコードでもないので直接取り出すことを選択しています。  

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

今回はevolutionsのテーブルも対象に取られているため、かなり`うわっ...`となるファイルになっていると思いますが、Tweet部分に限れば以下の部分だけです。   

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
ついでにシステムから利用するためのサンプルデータも追加しています。  

```sql
-- Tweet schema

-- !Ups
CREATE TABLE tweet (
    id         BIGINT(20)    NOT NULL AUTO_INCREMENT,
    content    VARCHAR(120)  NOT NULL,
    posted_at  DATETIME      NOT NULL,
    created_at TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
);

-- sample data
INSERT INTO tweet(id, content, posted_at) VALUES
(1, 'tweet1', '2020-03-15 13:15:00.012345'),
(2, 'tweet2', '2020-03-15 14:15:00.012345'),
(3, 'tweet3', '2020-03-15 15:15:00.012345'),
(4, 'tweet4', '2020-03-15 16:15:00.012345'),
(5, 'tweet5', '2020-03-15 17:15:00.012345');

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
          case _                    => super.rawType
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

<a id="markdown-codegenで自動生成される日付型の型を変更" name="codegenで自動生成される日付型の型を変更"></a>
##### Codegenで自動生成される日付型の型を変更

本セクションでの目的である日付型の変換をしている部分はここです。  

```scala
override def Column = new Column(_){
  // datetimeはデファルトでjava.sql.Timestamp型になるので、LocalDateTimeに書き換え
  override def rawType = model.tpe match {
    case "java.sql.Timestamp" => "LocalDateTime"
    case _                    => super.rawType
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

`output/codegen/com/example/Tables.scala`
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

<a id="markdown-slickのモデルを実装" name="slickのモデルを実装"></a>
## slickのモデルを実装

基本的なslickの設定が完了したので、evolutionsで作成されたモデルなどを利用しながら実際のシステムで利用するモデルを作成してきます。  
実装方法が何種類かあるのですが、利用してるRDBに応じて少し対応方法が変わります。  
今回はMySQLを利用しているのでそれ前提で記載していきます。  

<a id="markdown-slick33とmysqlを組み合わせた場合の日付型対応" name="slick33とmysqlを組み合わせた場合の日付型対応"></a>
### Slick3.3とMySQLを組み合わせた場合の日付型対応

SlickはRDBごとに日付関連を扱うときに利用する型が違っています。  
特にMySQLはほぼ全て文字列として取り扱おうとするため、そのままの状態では利用しづらくなってしまいます。  
今回はこの部分を自前の追加実装で吸収していきたいと思います。  
対応方法は何種類かあるのですが、今回はそのうち採用していく方法の実装のみ行います。  
おまけ部分で他の実装方法についても紹介しますので、気になる方はおまけをご覧ください。  

<a id="markdown-日付関連のrdbごと比較" name="日付関連のrdbごと比較"></a>
#### 日付関連のRDBごと比較

RDBごとに違うとありましたが、実際に確認してみます。  
以下のリンクから公式サイトの情報が確認できますが、リンク先の情報の画像も合わせて載せておきます。  
[https://scala-slick.org/doc/3.3.1/upgrade.html#support-for-java.time-columns](https://scala-slick.org/doc/3.3.1/upgrade.html#support-for-java.time-columns)

[MySQLの場合]  
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/03_profile_mysql.png" width="450">

[Postgresの場合]  
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/04_profile_postgres.png" width="450">

[Oracleの場合]
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/05_profile_oracle.png" width="450">

このようにそれぞれ日付型の型に割り当てられたSQL Typeに差があります。  
細かいことは不明ですが厳密にやろうとするとMySQLが日付関連のデータの持ち方に振れ幅が大きくてparserが統一できなかったのかもしれないですね。 (わかりませんが)  

<a id="markdown-未対応の場合のエラーと原因" name="未対応の場合のエラーと原因"></a>
### 未対応の場合のエラーと原因

特に何も対応せずに今のままの状態で実装を進めるとDBアクセスを行なったタイミングで以下のようなエラーになります。  

```sh
play.api.http.HttpErrorHandlerExceptions$$anon$1: Execution exception[[DateTimeParseException: Text '2020-03-15 13:15:00' could not be parsed at index 10]]
        at play.api.http.HttpErrorHandlerExceptions$.throwableToUsefulException(HttpErrorHandler.scala:332)
        at play.api.http.DefaultHttpErrorHandler.onServerError(HttpErrorHandler.scala:251)
        at play.core.server.AkkaHttpServer$$anonfun$2.applyOrElse(AkkaHttpServer.scala:421)
        at play.core.server.AkkaHttpServer$$anonfun$2.applyOrElse(AkkaHttpServer.scala:417)
        at scala.concurrent.impl.Promise$Transformation.run(Promise.scala:453)
        at akka.dispatch.BatchingExecutor$AbstractBatch.processBatch(BatchingExecutor.scala:55)
        at akka.dispatch.BatchingExecutor$BlockableBatch.$anonfun$run$1(BatchingExecutor.scala:92)
        at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
        at scala.concurrent.BlockContext$.withBlockContext(BlockContext.scala:94)
        at akka.dispatch.BatchingExecutor$BlockableBatch.run(BatchingExecutor.scala:92)
Caused by: java.time.format.DateTimeParseException: Text '2020-03-15 13:15:00' could not be parsed at index 10
        at java.time.format.DateTimeFormatter.parseResolved0(DateTimeFormatter.java:1949)
        at java.time.format.DateTimeFormatter.parse(DateTimeFormatter.java:1851)
        at java.time.LocalDateTime.parse(LocalDateTime.java:492)
        at java.time.LocalDateTime.parse(LocalDateTime.java:477)
        at slick.jdbc.MySQLProfile$JdbcTypes$$anon$4.getValue(MySQLProfile.scala:404)
        at slick.jdbc.MySQLProfile$JdbcTypes$$anon$4.getValue(MySQLProfile.scala:389)
        at slick.jdbc.SpecializedJdbcResultConverter$$anon$1.read(SpecializedJdbcResultConverters.scala:26)
        at slick.jdbc.SpecializedJdbcResultConverter$$anon$1.read(SpecializedJdbcResultConverters.scala:24)
        at slick.relational.ProductResultConverter.read(ResultConverter.scala:54)
        at slick.relational.ProductResultConverter.read(ResultConverter.scala:44)
```

重要な部分は以下の部分です。  
`Execution exception[[DateTimeParseException: Text '2020-03-15 13:15:00' could not be parsed at index 10]]`  

日付型のparseでエラーになっていますね。  

ここから少し難しくなるのですが、このエラーを調査していきます。  

エラー調査のために、先ほどのログをもう少し細かくみてみましょう。  
そうすると以下のような出力を見つけることができると思います。  
`at slick.jdbc.MySQLProfile$JdbcTypes$$anon$4.getValue(MySQLProfile.scala:389)`  

これが今回エラーが発生している箇所です。  
この`slic.jdbc.MySQLProfile`がどこで利用されているかというと、codegenで自動生成したModelにあります。  

codegenで生成されたコードを見てみると、以下のようにMySQLProfileを利用しているところがありますね。  
```scala
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables
```

ここで読み込んだprofileで実装されているLocalDateTimeの`getValue`というところに問題があるというわけです。  

では、引き続きコードを追ってみましょう。  
早速MySQLProfileの`getValue`を見ていきます。  
以下が、そのコードです。  
```scala
    override val localDateTimeType : LocalDateTimeJdbcType = new LocalDateTimeJdbcType {
      override def sqlType : Int = {
        java.sql.Types.VARCHAR
      }
      override def setValue(v: LocalDateTime, p: PreparedStatement, idx: Int) : Unit = {
        p.setString(idx, if (v == null) null else v.toString)
      }
      // 今回エラーが発生しているのはこのメソッド
      override def getValue(r: ResultSet, idx: Int) : LocalDateTime = {
        r.getString(idx) match {
          case null => null
          // 具体的にはこの parse 処理部分です。
          case iso8601String => LocalDateTime.parse(iso8601String)
        }
      }
      override def updateValue(v: LocalDateTime, r: ResultSet, idx: Int) = {
        r.updateString(idx, if (v == null) null else v.toString)
      }
      override def valueToSQLLiteral(value: LocalDateTime) : String = {
        stringToMySqlString(value.toString)
      }
    }
```

LocalDateTimeの値を`getValue`を見てください。  
この中でDBから受け取った日付の文字列をparseしています。  
今回はここでエラーが出てしまうということなのです。  

LocalDateTime.parseのデフォルトフォーマットは`yyyy-MM-ddTHH:mm:ss`というフォーマットになっているため、このフォーマットに合わない日付文字列は全てparseで落ちてしまいます。  
今回の場合`yyyy-MM-dd HH:mm:ss`の文字列で渡ってしまうため`T`が足りておらず、エラーになってしまうわけですね。  
indexもちょうど10番目です。  

エラーの内容と原因がわかったので、次はこれを解決していきましょう。  

<a id="markdown-localdatetimeのparseエラー対応方針決め" name="localdatetimeのparseエラー対応方針決め"></a>
### LocalDateTimeのparseエラー対応方針決め

ここで対応方針を決める必要があります。  
詳細はここでは割愛しますが、対応方針として考えられるものとして3つほどあります。

1. 一度Stringで受け取るようにしてModel <-> DB Valeの変換を`def *`などで自前実装する
1. MappedColumnTypeを利用して、implict valの形でマッピングを作成する
1. MySQLProfileを継承して独自Profileを作成する (公式推奨)

それぞれメリット/デメリット向いている用途などがありますが、今回は３番目の独自Profileを実装する方式で対応をしていきたいと思います。  

この方法を選択する理由はシンプルに`公式推奨`だからです。  
私は一番理解しやすいのは1番だと考えていて、それがrookies資料としては適切なのではと悩みました。  
ただ、全てのテーブルのモデルのmappingを書いていくのは効率が悪すぎるのと、公式が推奨する形が一番御行儀が良いと思うので、Profile拡張で作成をしていきたいと思います。  

とはいえ、Profile拡張という言葉の持つパワーのせいで難しい気がするだけで、実は`LocalDateTime.parse`の引数に渡すformatterを実装するだけという超シンプル対応でもあります。  
あまり難しく考えずに「既存実装コピペしてLocalDateTimeのformatterだけ直す」と思っていただければ、心理的負荷は減るのかなと思います。  

<a id="markdown-独自profile実装" name="独自profile実装"></a>
### 独自Profile実装

では、独自Profileの実装をしていきましょう。  
先ほどお話ししたように公式に実装方法が書かれているので、そちらを参照していきます。  
[https://scala-slick.org/doc/3.3.1/upgrade.html#support-for-java.time-columns](https://scala-slick.org/doc/3.3.1/upgrade.html#support-for-java.time-columns)

文章でいうと以下の部分ですね。  
```
If you need to customise these formats, you can by extending a Profile and overriding the appropriate methods.
For an example of this see: https://github.com/d6y/instant-etc/blob/master/src/main/scala/main.scala#L9-L45.
Also of use will be an example of a full mapping, such as: https://github.com/slick/slick/blob/v3.3.0/slick/src/main/scala/slick/jdbc/JdbcTypesComponent.scala#L187-L365.
```

この公式実装はH2DBを元に書かれているので、これを参考にMySQLのコードを作っていきます。  
公式の拡張実装と、以下のMySQLProfileの実装を比較しながら対応していくとわかりやすいと思います。  
[MySQLProfile.scala#L389-L415](https://github.com/slick/slick/blob/master/slick/src/main/scala/slick/jdbc/MySQLProfile.scala#L389-L415)

では、以下に最終的な実装を記載します。  

`app/slick/profile/MyDBProfile.scala`
```scala
package slick.profile

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

/* LocalDateTimeをプロダクトに適した形に処理できるようにProfile設定を独自に拡張 */
trait MyDBProfile extends slick.jdbc.JdbcProfile with slick.jdbc.MySQLProfile {
  import java.sql.{PreparedStatement, ResultSet}
  import slick.ast.FieldSymbol

  @inline
  private[this] def stringToMySqlString(value : String) : String = {
    value match {
      case null => "NULL"
      case _ =>
        val sb = new StringBuilder
        sb append '\''
        for(c <- value) c match {
          case '\'' => sb append "\\'"
          case '"' => sb append "\\\""
          case 0 => sb append "\\0"
          case 26 => sb append "\\Z"
          case '\b' => sb append "\\b"
          case '\n' => sb append "\\n"
          case '\r' => sb append "\\r"
          case '\t' => sb append "\\t"
          case '\\' => sb append "\\\\"
          case _ => sb append c
        }
        sb append '\''
        sb.toString
    }
  }

  override val columnTypes = new JdbcTypes

  // Customise the types...
  class JdbcTypes extends super.JdbcTypes {

   // PostgresのProfileを参考にミリ秒も含めて対応できるformatterを実装
   private[this] val formatter = {
      new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND,0,9,true)
        .optionalEnd()
        .toFormatter()
    }

    override val localDateTimeType : LocalDateTimeJdbcType = new LocalDateTimeJdbcType {
      override def sqlType : Int = {
        java.sql.Types.VARCHAR
      }

      override def setValue(v: LocalDateTime, p: PreparedStatement, idx: Int) : Unit = {
        p.setString(idx, if (v == null) null else v.toString)
      }
      override def getValue(r: ResultSet, idx: Int) : LocalDateTime = {
        r.getString(idx) match {
          case null       => null
          // 文字列から日付型にパースできるようにparseにformatterを渡す
          case dateString => LocalDateTime.parse(dateString, formatter)
        }
      }
      override def updateValue(v: LocalDateTime, r: ResultSet, idx: Int) = {
        r.updateString(idx, if (v == null) null else v.toString)
      }
      override def valueToSQLLiteral(value: LocalDateTime) : String = {
        stringToMySqlString(value.toString)
      }
    }
  }
}

object MyDBProfile extends MyDBProfile
```

結構なコード量に見えますが、ほとんどコピペしただけです。  
大事な部分は以下のパース部分ですね。  
```
case dateString => LocalDateTime.parse(dateString, formatter)
```

ほとんどは元のMySQLProfileの実装をコピーして持ってきているだけです。  
しかしLocalDateTimeのparse処理が修正されているので、このProfileを利用してSlickに設定すれば特に他には何もすることなくLocalDateTimeがモデルにマッピングできるようになります。  

ちょっと話は逸れますが`.appendFraction`便利ですね。  
LocalDateTimeのparseはミリ秒については一律で設定する方法がなく、ミリ秒の数だけ`SSSSSS`みたいに書かないといけないのでLocalDateTime側のメソッドで処理してもらえると助かります。  
また0-9までの指定になっているのは、MySQL側でミリ秒以下が1つでもあると0埋めして9桁で受け取ろうとするからです。  
MySQL自体は6桁までの精度しかない(はず)です。  

少し長くなりましたが日付対応はこれで完了です。  

<a id="markdown-play-slickを利用してモデルの操作を行う" name="play-slickを利用してモデルの操作を行う"></a>
### play-slickを利用してモデルの操作を行う

最後に今まで作成したモデルやProfileを利用して、slickへ問合せを行うためのモデルを作成していきます。  
ここまでの実装はplay-slickは関係なくslickのレイヤーでの実装でした。  
ここからはplay-slickの機能を利用して、モデル作成から簡単なデータ取得まで進めてみたいと思います。  

<a id="markdown-そもそもplay-slickとは" name="そもそもplay-slickとは"></a>
#### そもそもplay-slickとは？

実は私あまりよくわかっていなかったのですが、そもそもplay-slickとはなんなのでしょうか。  
と言うことで、公式情報を引用させていただきます。

```
The Play Slick module makes Slick a first-class citizen of Play, and consists of two primary features:

・Integration of Slick into Play’s application lifecycle.
・Support for Play database evolutions.
```

evolutionsをサポートしているというのは、比較的どうでもいいのでもう片方に注目します。  

私もなんとなく思ってましたが、やっぱりslickをplayのライフサイクルの中に組み込んでくれるのがplay-slickのようです。  
設定情報からDB connectionを作成したり破棄したりの管理を良い感じにやってくれるもの、くらいの認識で良いのではないかと思います。  

なので、他のモデルであったり先ほどまで作成していたprofileなどライフサイクルと関係ない部分は通常のslickと変わらないと言うことですね。  

<a id="markdown-play-slickのコード上の機能" name="play-slickのコード上の機能"></a>
#### play-slickのコード上の機能

では、そのライフサイクルが云々というのがコード上どうなるかを確認してみます。  
公式サイトのページでは[こちらを参照](https://www.playframework.com/documentation/2.8.x/PlaySlick#DatabaseConfig-via-runtime-dependency-injection)  

公式サイトの例ではcontrollerに対してDIするような実装になっていますね。  
以下はあくまで「サンプルコード」で実際に利用するコードではありませんが、この実装を今の`TweetController`に適用する以下のような実装になります。(実装する必要はありません)

`app/controllers/tweet/TweetController.scala`
```scala
@Singleton
class TweetController @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider, // play-slick
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
extends BaseController
with I18nSupport
with HasDatabaseConfigProvider[JdbcProfile]{ // play-slick

  // HasDatabaseConfigProviderの持つprofileからapiを取得
  // slickでいうところのslick.jdbc.MySQLProfile.api._
  import profile.api._

// ...省略...
}

```

`// play-slick` とコメントを書いた部分がplay-slick用の実装です。  
実装の実態は`HasDatabaseConfigProvider`が持っていて、その中で利用されている変数として`DatabaseConfigProvider`がいます。  
この`DatabaseConfigProvider`をplay-slickが設定ファイルから組み立ててInject、その結果適切なprofileを読み取れるようになるという仕組みです。  

Controllerクラス内部の`import dbConfig.profile.api._`の部分がProfileからslickの処理に必要な機能をimportしている部分になります。  
コメントにも記載がありますが、そのままのslickだと`import slick.profile.MySQLProfile.api._`のように利用してるものにあたります。  

play-slickを利用することでRDBの違いを簡単に設定ファイルに隠蔽することができますね。  

では、このplay-slickを利用して実際にコードを書いていきます。  
今回はplay-slickのサンプルプロジェクトを参考に`Repository`レイヤーを作成する形で実装を行っていきます。  
ここで紹介した`DatabaseConfigProvider`関連のDIもControllerではなくRepositoryに対して行っていきましょう。  

<a id="markdown-tweet関連のdbアクセス用クラスを作成する" name="tweet関連のdbアクセス用クラスを作成する"></a>
#### Tweet関連のDBアクセス用クラスを作成する

では、さっそくTweet Tableにアクセスするために必要なクラスを作成していきます。  
slick-codegeで作成したクラスや、play-slickのサンプルを参考にしながら作成していきます。  

まずはモデル作成していくのですが、元々作成されていたTweetモデルを移動させて現在のtableに合わせて調整をします。  

`app/models/Tweet.scala` -> `app/slick/models/Tweet.scala`  
```scala
package slick.models

import java.time.LocalDateTime

// case classについての説明は省略
// 参考: https://docs.scala-lang.org/ja/tour/case-classes.html
case class Tweet(
  id:        Option[Long],
  content:   String,
  postedAt:  LocalDateTime = LocalDateTime.now,
  createdAt: LocalDateTime = LocalDateTime.now,
  updatedAt: LocalDateTime = LocalDateTime.now
)
```

※ コンパイルするとmodels.Tweetを利用していた箇所でエラーになるので、適宜修正してください。slick.をつけてあげるだけで大丈夫なはずです。  

次にRepositoryを作成していきましょう。  
この実際は以下のページを参考に実装しています。  
[Play公式のサンプルとして引用されている実装](https://github.com/playframework/play-slick/blob/master/samples/basic/app/dao/CatDAO.scala)  


`app/slick/repositories/TweetRepository.scala`
```scala
package slick.repositories

import java.time.LocalDateTime
import play.api.db.slick.{HasDatabaseConfigProvider,DatabaseConfigProvider}
import javax.inject.{Inject, Singleton}
import slick.jdbc.{JdbcProfile, GetResult}
import scala.concurrent.{Future, ExecutionContext}
import slick.models.Tweet

@Singleton
class TweetRepository @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val tweet = new TableQuery(tag => new TweetTable(tag))

  // ########## [DBIO Methods] ##########

  /**
    * tweetを全件取得
    */
  def all(): Future[Seq[Tweet]] = db.run(tweet.result)

  // ########## [Table Mapping] ##########
  private class TweetTable(_tableTag: Tag) extends Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {

    // Tableとのカラムマッピング
    val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")

    // Plain SQLでデータ取得を行う用のマッピング
    implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[Tweet] = GetResult{
      prs => import prs._
      Tweet.tupled((Some(<<[Long]), <<[String], <<[LocalDateTime], <<[LocalDateTime], <<[LocalDateTime]))
    }

    // model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
    // O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
    def * = (id.?, content, postedAt, createdAt, updatedAt) <> (Tweet.tupled, Tweet.unapply)

    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((Option(_1.get), _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  }
}
```

少し長くなっていますが、一つずつ説明していきます。  
まずクラス宣言の部分です。  

```scala
@Singleton
class TweetRepository @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
```

この部分は前段で説明したControllerの実装とほぼ同じです。  
Repositoryはインスタンスを複数持つ必要がない(とおもう)ので`@Singleton` を付与して、Singletonオブジェクトにしています。  

また今回`(implicit ec: ExecutionContext)`の記述も追加しています。  
slickは問合せ結果を`Future`型で返してくるため、Futureを利用するために必要なExecutionContextが必要になります。  
そこでPlay標準で用意されているExecutionContextを利用できるように、クラス宣言時にDIで受け取るようにしています。  
この辺はScalaの話になってしまうので本ハンズオンでは割愛します。  

次に実装の下の部分にあるTable Mappingのブロックです。  

```scala
// ########## [Table Mapping] ##########
private class TweetTable(_tableTag: Tag) extends Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {

  // Tableとのカラムマッピング
  val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
  val postedAt:  Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
  val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
  val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")

  // Plain SQLでデータ取得を行う用のマッピング
  implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[Tweet] = GetResult{
    prs => import prs._
    Tweet.tupled((Some(<<[Long]), <<[String], <<[LocalDateTime], <<[LocalDateTime], <<[LocalDateTime]))
  }

  // model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
  // O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
  def * = (id.?, content, postedAt, createdAt, updatedAt) <> (Tweet.tupled, Tweet.unapply)

  // Maps whole row to an option. Useful for outer joins.
  def ? = ((
    Rep.Some(id),
    Rep.Some(content),
    Rep.Some(postedAt),
    Rep.Some(createdAt),
    Rep.Some(updatedAt)
  )).shaped.<>(
  { r =>
    import r._;
    _1.map( _=>
        Tweet.tupled((
          Option(_1.get), // モデル側はidがOptionなのでOptionで包んでいる
          _2.get,
          _3.get,
          _4.get,
          _5.get
        ))
  )},
  (_:Any) =>
    throw new Exception("Inserting into ? projection not supported.")
  )
}
```

これはslick-codegenで作成されたものを参考に修正を行っています。  

以下の部分がTableとScala側で利用するためのデータ型のマッピングです。  
`column[LocalDateTime]`の部分が、独自で作成してMyDBProfileのおかげでマッピングできるようになっている場所です。  
この部分が通常のORMのモデル定義風ですよね。  

```scala
// Tableとのカラムマッピング
val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
val postedAt:  Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
```

次に以下の部分。  
```scala
// Plain SQLでデータ取得を行う用のマッピング
implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[Tweet] = GetResult{
  prs => import prs._
  Tweet.tupled((Some(<<[Long]), <<[String], <<[LocalDateTime], <<[LocalDateTime], <<[LocalDateTime]))
}
```

これはslickからPlain SQLを実行するときに利用されるマッピングです。  
Plain SQLというのは以下のような方法でのDBIO実行です。  
```scala
sql"SELECT * FROM tweet".as[Tweet]
```

このas句を理解するために必要なimplicitというわけですね。  
これがない場合には `as[(Long, String, LocalDateTime, LocalDateTime, LocalDateTime)]`のようにタプルで指定して利用する形になります。  

なので、この実装は必ずしも必須の実装ではありません。  

次に`def *`  
これが一番重要で、slickからデータベースへアクセスしたときに通常利用されるマッピングの定義です。  

```scala
// model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
// O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
def * = (id.?, content, postedAt, createdAt, updatedAt) <> (Tweet.tupled, Tweet.unapply)
```

ここに定義した内容でselect, insert, updateなどの処理をDB側に上手くマッピングしてデータの流し込みや受け取りが行われます。  
`id.?`の部分ですが、これはPrimaryKeyに対して行える呼び出し方になります。  
主キーはAutoIncで自動採番にしてinsert時にはプログラムから指定せず、DB側で連番を付与させることが多いですよね。  
そのためSlickでも登録時にはOptionであることを許容して、select時には非Optionの型で処理できるように`.?`という定義の仕方を用意してくれています。  
これを利用することでかなり定義をシンプルにかけるので、覚えておいてください。  

ちなみにtupledですんなり処理できない場合には、以下のように書くことができます。  

```scala
// model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
def * = (id, content, postedAt, createdAt, updatedAt) <> (
  (x: (Long, String, LocalDateTime, LocalDateTime, LocalDateTime)) => {
    Tweet(Some(x._1), x._2 ,x._3, x._4, x._5)
  },
  (tweet: Tweet) => {
    Some((tweet.id.getOrElse(0L), tweet.content, tweet.postedAt, tweet.createdAt, tweet.updatedAt))
  }
)
```

これは`id.?`を使わなかった場合の書き方の一例みたいな形ですが、やっていることはtupled, unapplyを自分で書いているということです。  

そして最後に`def ?`です。  

```scala
// Maps whole row to an option. Useful for outer joins.
def ? = ((
  Rep.Some(id),
  Rep.Some(content),
  Rep.Some(postedAt),
  Rep.Some(createdAt),
  Rep.Some(updatedAt)
)).shaped.<>(
{ r =>
  import r._;
  _1.map( _=>
      Tweet.tupled((
        Some(_1.get), // モデル側はidがOptionなのでOptionで包んでいる
        _2.get,
        _3.get,
        _4.get,
        _5.get
      ))
)},
(_:Any) =>
  throw new Exception("Inserting into ? projection not supported.")
)
```
ここもコメントにあるように、idの部分をOptionで包むようにしています。  
この実装は実は私もよくわかっていないのですが、データがないときにもなんかいい感じにしてくれるっぽいことが書いてありますね。  

これでDBへアクセスするためのクラスが作成できました。  

<a id="markdown-独自実装したprofileを利用するように設定する" name="独自実装したprofileを利用するように設定する"></a>
#### 独自実装したprofileを利用するように設定する

必要な実装は終わりましたがplay-slick関連の実装の中でprofileを指定する場所が見当たりませんでしたね。  
ここでは独自実装したprofileを利用してslickが動いてくれるように設定をしていきます。  

といっても、行うことは単純で`application.conf`を修正するだけです。  

`conf/application.conf`
```
slick {
  dbs {
    default {
      # 独自実装したprofileを指定
      profile = "slick.profile.MyDBProfile$"
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
```

slick関連の設定だけ抜粋しています。  
`slick.dbs.default.profile`の部分を修正しているだけです。  
これによってplay-slickがこの設定を読みに行ってくれるようになります。  

confの設定はこれで完了です。  

これでslickに関する実装は完了です！  

次の章からは、これらを利用してCRUDを修正していきます。  

<a id="markdown-おまけ" name="おまけ"></a>
## おまけ

ここからはslickのマッピングの別の書き方を紹介します。  
今回はあくまでLocalDateTimeに対しての記載になりますが、独自型やその他についてもこれらを利用して対応できます。  

<a id="markdown-mappedcolumntypeを利用したマッピング" name="mappedcolumntypeを利用したマッピング"></a>
### MappedColumnTypeを利用したマッピング

通常であればslickはこの方法で外部の型をマッピングできるようにしていきます。  
ただし、今回のLocalDateTimeについては例外でこの方法ではシンプルに実装できませんでした。  

この実装については[こちら](https://qiita.com/lightstaff/items/cb247f98b9bb12213de9)の記事を参考にさせていただいています。

実装を見た方が早いので、コードを載せます。  

`app/slick/samples/SlickMappedMySQLDateTime.scala`
```scala
package slick.samples

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/* MySQLDateTimeを直接マッピング対象にできないので、mappingに利用するクラスを作成する */
case class MySQLDateTime(v: String) {
  def toLocalDateTime: LocalDateTime = LocalDateTime.parse(v, MySQLDateTime.format)
}

object MySQLDateTime {
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(time: LocalDateTime): MySQLDateTime = MySQLDateTime(time.format(format))
}


trait LocalDateTimeColumMapper {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._

  implicit lazy val localDateTimeMapper = MappedColumnType.base[MySQLDateTime, String] (
    { ldt => ldt.v },
    { str => MySQLDateTime(str)}
  )
}
```

まず`MySQLDateTime`という`case class`を実装しています。  
ここが使いづらい原因になっている箇所です。  

本当は直接LocalDateTimeに紐づけたいのですが、MySQLProfileにでLocalDateTimeへのマッピングが用意されているので、ここで作成したMappedColumnTypeより先にMySQLProfileのgetValue処理が呼び出されてしまうようでした。  


例えば以下のようなMappingを、作成して。  

```scala
MappedColumnType.base[LocalDateTime, String] (
    { ldt => ldt.toString },
    { str => LocalDateTime.parse(str, formatter)}
  )
```

さらにモデル側で以下のように宣言をしてみます。  

`val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")`

この場合Profileに定義されているLocalDateTimeのマッピングが先に処理されて、その後にMappedColumnTypeの処理に移ろうします。  
なので結局はgetValueのLocalDateTime.parseの箇所で落ちてしまうんです。  

かといってStringに対してのmappingにしてしまうと、普通にStringで使いたいものについても変換がかかってしまいます。  
そのためLocalDateTimeを直接使うことができません。  

そのため一度経由するためのクラスとして独自の`MySQLDateTime`という型が必要になってしまったと言うわけです。  
既に若干冗長になってしまいました。  

しかし、この実装だとまたもうちょっと大変なところがあります。  
モデル側の実装を見てみましょう。  

`app/slick/samples/SlickMappedTweetV1.scala`
```scala
package slick.samples

import java.time.LocalDateTime
import slick.jdbc.{GetResult}
import slick.models.Tweet

// ...一部を抜粋...

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[MySQLDateTime]): GetResult[Tweet] = GetResult{
    prs => import prs._
    Tweet.tupled((
      <<[Option[Long]],
      <<[String],
      <<[MySQLDateTime].toLocalDateTime,
      <<[MySQLDateTime].toLocalDateTime,
      <<[MySQLDateTime].toLocalDateTime
    ))
  }

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
 class SlickMappedTweetTableV1(_tableTag: Tag) extends profile.api.Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {

    def * = (id, content, postedAt, createdAt, updatedAt) <> (
      (x: (Long, String, MySQLDateTime, MySQLDateTime, MySQLDateTime)) => {
        Tweet(
          Some(x._1),
          x._2,
          x._3.toLocalDateTime,
          x._4.toLocalDateTime,
          x._5.toLocalDateTime
        )
      },
      (tweet: Tweet) => {
        Some((
          tweet.id.getOrElse(0L),
          tweet.content,
          MySQLDateTime(tweet.postedAt.toString),
          MySQLDateTime(tweet.createdAt.toString),
          MySQLDateTime(tweet.updatedAt.toString)
        ))
      }
    )

    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((Option(_1.get), _2.get, MySQLDateTime(_3.get.toString).toLocalDateTime, MySQLDateTime(_4.get.toString).toLocalDateTime, MySQLDateTime(_5.get.toString).toLocalDateTime)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

```

実装を見るとわかりますが、せっかくMappedColumnTypeをしているのにモデル側でも取り回しの処理をケアしてあげないといけない状態になっています。  

このようにMappedColumnTypeでの実装だと、必要になるコード量は増えるのに結局ケアもしないといけないということで手間が倍に増えてしまいました。  
そのため今回のケースについては適切ではなさそうです。  

<a id="markdown-stringのまま受け取って個別にマッピング" name="stringのまま受け取って個別にマッピング"></a>
### Stringのまま受け取って個別にマッピング

こちらは実装の中身を理解しやすく、手間は少しかかりますがシンプルな実装だと思います。  

`app/slick/samples/TupleMappedTweet.scala`
```scala
package slick.samples

import java.time.LocalDateTime
import slick.jdbc.{GetResult}
import java.time.format.DateTimeFormatter
import slick.models.Tweet

/* def *のtupleでマッピングをするサンプル実装 */
object TupleMappedTweetTable extends {
  val profile    = slick.jdbc.MySQLProfile
} with TupleMappedTweetTable

trait TupleMappedTweetTable {
  val profile: slick.jdbc.JdbcProfile
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  import profile.api._

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String]): GetResult[Tweet] = GetResult{
    prs => import prs._
    Tweet.tupled((
      <<[Option[Long]],
      <<[String],
      LocalDateTime.parse(<<[String], format),
      LocalDateTime.parse(<<[String], format),
      LocalDateTime.parse(<<[String], format)
    ))
  }

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  class TupleMappedTweetTable(_tableTag: Tag) extends profile.api.Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {
    def * = (id, content, postedAt, createdAt, updatedAt) <> (
      (x: (Long, String, String, String, String)) => {
        Tweet(
          Some(x._1),
          x._2,
          LocalDateTime.parse(x._3, format),
          LocalDateTime.parse(x._4, format),
          LocalDateTime.parse(x._5, format)
        )
      },
      (tweet: Tweet) => {
        Some((tweet.id.getOrElse(0L), tweet.content, tweet.postedAt.toString, tweet.createdAt.toString, tweet.updatedAt.toString))
      }
    )

    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((Option(_1.get), _2.get, LocalDateTime.parse(_3.get, format), LocalDateTime.parse(_4.get, format), LocalDateTime.parse(_5.get, format))))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id:        Rep[Long]   = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String] = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[String] = column[String]("posted_at")
    val createdAt: Rep[String] = column[String]("created_at")
    val updatedAt: Rep[String] = column[String]("updated_at")
  }

  lazy val query = new TableQuery(tag => new TupleMappedTweetTable(tag))
}
```

これは`def *`などなど、変換が必要な場所それぞれで丁寧に処理していくパターンですね。  
対応方法としてはわかりやすいのかなと思います。  

ただ、全てのモデルや全ての日付型のマッピングでコツコツ実装をしてあげないといけないのでテーブル数や日付型データを扱う数が増えると大変です。  

そのため今回のケースについては、やはり好ましい実装ではなさそうです。  

