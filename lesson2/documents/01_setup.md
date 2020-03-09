<a id="markdown-lesson-2" name="lesson-2"></a>
# Lesson 2

ハンズオンで開発を行うための環境をセットアップします。  
本リポジトリをCloneして利用する際には、このLessonは不要です。  
個人で1からプロジェクトを作成する際に参考にしていただければと思います。  

<a id="markdown-目次" name="目次"></a>
# 目次

<!-- TOC -->

- [Lesson 2](#lesson-2)
- [目次](#目次)
    - [Playframeworkの実行環境のセットアップ](#playframeworkの実行環境のセットアップ)
        - [Dockerのインストール](#dockerのインストール)
        - [Playframeworkの初期プロジェクトのダウンロード](#playframeworkの初期プロジェクトのダウンロード)
        - [プロジェクトの配置](#プロジェクトの配置)
        - [DockerでPlay-Scalaの実行環境をセットアップ](#dockerでplay-scalaの実行環境をセットアップ)
    - [DBのセットアップ](#dbのセットアップ)
        - [Docker Serviceの設定追加](#docker-serviceの設定追加)
            - [Dockerfileの設定](#dockerfileの設定)
            - [my.cnfを配置](#mycnfを配置)
            - [docker-composeにDBの設定を追記](#docker-composeにdbの設定を追記)
    - [Playframeworkのセットアップ](#playframeworkのセットアップ)
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


<a id="markdown-playframeworkの実行環境のセットアップ" name="playframeworkの実行環境のセットアップ"></a>
## Playframeworkの実行環境のセットアップ

<a id="markdown-dockerのインストール" name="dockerのインストール"></a>
### Dockerのインストール
環境はDockerを利用してセットアップを行うため、Dockerがインストールされていない場合にはDockerのインストールを行ってください。  
Windows: [Download](https://docs.docker.com/docker-for-windows/install/)  
Mac:     [Download](https://docs.docker.com/docker-for-mac/install/)  

<a id="markdown-playframeworkの初期プロジェクトのダウンロード" name="playframeworkの初期プロジェクトのダウンロード"></a>
### Playframeworkの初期プロジェクトのダウンロード
sbtコマンドでのセットアップも可能です。
しかしsbtがインストールされてないケースもあるため、今回はLightbend社のサイトからダウンロードを行ってください。  
[Download](https://developer.lightbend.com/start/?group=play&project=play-samples-play-scala-hello-world-tutorial)  

<a id="markdown-プロジェクトの配置" name="プロジェクトの配置"></a>
### プロジェクトの配置
ダウンロードしたファイルを展開し、プロジェクトのルートに配置します。  
今回はproject_rootフォルダを作成済みのため、ダウンロードファイルが展開された後の中身だけを配置します。  

<a id="markdown-dockerでplay-scalaの実行環境をセットアップ" name="dockerでplay-scalaの実行環境をセットアップ"></a>
### DockerでPlay-Scalaの実行環境をセットアップ
project_root直下にdocker-compose.yamlファイルを作成  
以下の内容をdocker-composeファイルに貼り付け  

```yml
# docker-composeの構造のバージョンを指定 (現時点の最新)
version: '3'
# dockerで利用したい各コンテナ(service)をまとめる要素
services:
  # play-scalaという名前をつけて、Serviceを設定。このコンテナはplayを動作させるためのコンテナになります
  play-scala:
    # 利用するimageを指定。今回はjava8系で動作するsbtの最新imageを指定しています
    image: hseeberger/scala-sbt:8u242_1.3.8_2.13.1
    # 特にこだわりはないので、service名とcontainer名を同じにしています。
    container_name: play-scala
    # playのデフォルト利用ポートが9000番なので9000を指定。hostからも9000でアクセスできるように設定しています
    ports:
      - "9000:9000"
    # 初回起動時にキャッシュされる依存ライブラリ群をキャッシュするためにvolumesに指定
    volumes:
      - .:/source
      - ./.ivy2:/root/.ivy2
      - ./.sbt:/root/.sbt
      - ./.cache:/root/.cache
    working_dir: /source
    # 端末に入って作業することが多いので、端末を割り当てる
    tty: true
    # 今後DB接続を行うため、ネットワークに属させる
    networks:
      - app-net

# 今後play, db間での通信があるためネットワークを構築
networks:
  app-net:
    driver: bridge
```

貼り付けが行えたら、実際にコンテナを起動してplayが動作することを確認します。  

```sh
# project_root (docker-compose.yamlファイルが存在する場所) で以下のコマンドを実行
$ docker-compose up -d

# ... 起動まで待ちます ...
# 起動を以下のコマンドを実行
$ docker-compose ps

# 以下のように表示されれば、正常に起動しています。
#    Name      Command   State           Ports
# -----------------------------------------------------
# play-scala   bash      Up      0.0.0.0:9000->9000/tcp

```

コンテナの起動ができたら、実際にplayを起動してみましょう。  

```sh
# play-scalaのコンテナにはbashが入っているので、bashコンソールでアクセスします
# imageによってはbashが入っていないことがあるので、その時には sh などで試してみましょう
$ docker-compose exec play-scala bash

root@8f6b2156168d:/source# sbt
# ...
# ...
# ... 起動完了後に一度以下のエラーが出ますが、動作に支障はないので今回はスルーします
[error] server failed to start on local:///root/.sbt/1.0/server/d2c10f28878e8945c341/sock. java.io.IOException: com.sun.jna.LastErrorException: [36] File name too long

# sbtが起動したら run コマンドでサーバを起動してみましょう
[play-scala-hello-world-tutorial] $ run
# ...
# ...
# ... 以下が表示されたら起動完了
# --- (Running the application, auto-reloading is enabled) ---
#
# [info] p.c.s.AkkaHttpServer - Listening for HTTP on /0.0.0.0:9000
#
# (Server started, use Enter to stop and go back to the console...)

```

Playが起動したらhost側のブラウザから以下のurlからサーバにアクセスしてみましょう。  
[http://localhost:9000](http://localhost:9000)  

以下の画面が表示されれば起動は成功です。  
![play hello world](https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/play%E8%B5%B7%E5%8B%95.png)  

<a id="markdown-dbのセットアップ" name="dbのセットアップ"></a>
## DBのセットアップ

<a id="markdown-docker-serviceの設定追加" name="docker-serviceの設定追加"></a>
### Docker Serviceの設定追加

DB用のdocker-compose設定を行なっていきます。  
`project_root` 直下に以下のようにディレクトリ/ファイルを作成してください。  
`init.sql` を作成していますが、現状利用しないため中身は空のままとしておいてください。  

```sh
docker
└── db
    ├── Dockerfile
    ├── init
    │   └── init.sql
    ├── my.cnf
    └── mysql_data
```

<a id="markdown-dockerfileの設定" name="dockerfileの設定"></a>
#### Dockerfileの設定

`doker/db/Dockerfile` を編集していきます。  
以下のようにファイルに記載してください。  

```dockerfile
# mysqlは5.7を使用
FROM mysql:5.7

# imageがdebianのため、apt-getで日本のlocaleを追加
RUN apt-get update && \
    apt-get install -y locales && \
    rm -rf /var/lib/apt/lists/* && \
    echo "ja_JP.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8

# docker/db/my.cnfをdockerイメージ上の/etc/...にコピーして配置
COPY ./my.cnf /etc/mysql/conf.d/my.cnf
```

<a id="markdown-mycnfを配置" name="mycnfを配置"></a>
#### my.cnfを配置

先ほどのtreeの設定と同様の箇所にmy.cnfファイルを配置します。  
以下のようにファイルに記載してください。  

```
[mysqld]
character_set_server=utf8mb4
default_authentication_plugin=mysql_native_password
collation-server=utf8mb4_bin

[mysqldump]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4
```

<a id="markdown-docker-composeにdbの設定を追記" name="docker-composeにdbの設定を追記"></a>
#### docker-composeにDBの設定を追記

`docker-compose.yaml` にDBのService設定を追加します。  
以下の設定をファイルに追加してください。  

```yaml
  db:
    build: ./docker/db
    ports:
      - "3306:3306"
    container_name: db
    volumes:
      # 初期データを投入するSQLが格納されているdir
      - ./docker/db/init:/docker-entrypoint-initdb.d
      # 永続化するときにマウントするdir
      - ./docker/db/mysql_data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      # Container内にデータベースを作成 TODO: システム名は検討
      MYSQL_DATABASE: twitter_clone
    networks:
      - app-net
```

最終的に以下のようになっていればOKです。

```yaml
version: '3'
services:
  play-scala:
    image: hseeberger/scala-sbt:8u242_1.3.8_2.13.1
    container_name: play-scala
    ports:
      - "9000:9000"
    volumes:
      - .:/source
      - ./.ivy2:/root/.ivy2
      - ./.sbt:/root/.sbt
      - ./.cache:/root/.cache
    working_dir: /source
    tty: true
    networks:
      - app-net
  db:
    build: ./docker/db
    ports:
      - "3306:3306"
    container_name: db
    volumes:
      # 初期データを投入するSQLが格納されているdir
      - ./docker/db/init:/docker-entrypoint-initdb.d
      # データを永続化するときにマウントするdirを指定
      - ./docker/db/mysql_data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      # TODO: システム名は検討
      MYSQL_DATABASE:      twitter_clone
      # timezoneを設定
      TZ:                  Asia/Tokyo
    # play-scalaと同一ネットワーク上に置く
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

設定が完了したため、Serviceの動作を確認します。  

```sh
$ docker-compose up -d
# ... 起動まで待ちます ...

# ... 起動完了したら以下のコマンドでステータスを確認 ...
$ docker-compose ps
   Name                Command             State                 Ports
------------------------------------------------------------------------------------
db           docker-entrypoint.sh mysqld   Up      0.0.0.0:3306->3306/tcp, 33060/tcp
play-scala   bash                          Up      0.0.0.0:9000->9000/tcp

```

これでDockerの設定は完了です。  


<a id="markdown-playframeworkのセットアップ" name="playframeworkのセットアップ"></a>
## Playframeworkのセットアップ

<a id="markdown-buildsbtに依存関係を追加" name="buildsbtに依存関係を追加"></a>
### build.sbtに依存関係を追加

今回はplay-slickやslick-codegenを利用して環境を作成していきます。  
以下の依存関関係を`build.sbt`へ追加してください。  

```scala 
evolutions,
"com.typesafe.play"      %% "play-slick"            % "5.0.0",
"com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
// play-slickの5.0.0ではslick 3.3.2を利用しているため、codegenも同様に3.3.2を指定しています。
// https://github.com/playframework/play-slick#all-releases
"com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
```

依存関係を追加すると`build.sbt`ファイルは以下のようになります。  
```scala
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-hello-world-tutorial""",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      evolutions,
      "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
      "com.typesafe.play"      %% "play-slick"            % "5.0.0",
      "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
      "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
      // 3.3.2のドキュメントがまだ存在しない
      // https://scala-slick.org/doc/3.3.1/database.html 
      "mysql"                   % "mysql-connector-java"  % "6.0.6",
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
```

<a id="markdown-slick-evolutionsの設定" name="slick-evolutionsの設定"></a>
### slick-evolutionsの設定

TODO: slick-evolutionsの説明

<a id="markdown-dbへ接続するためにconfを設定" name="dbへ接続するためにconfを設定"></a>
#### DBへ接続するためにconfを設定

slickを利用するにあたって、DBの接続情報などのいわゆる`config`を記載していきます。  
playでは `conf/application.conf` が基本の設定ファイルになります。  
今回はmysqlを利用するので以下のように設定を記載してください。  

```
# project_root/conf/application.conf
slick.dbs {
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
```

<a id="markdown-設定の補足" name="設定の補足"></a>
##### 設定の補足

`slick.dbs.{db_name}.db` というような構造になっており、`db_name`の部分は任意に設定可能です。  
ここで設定した`db_name`がこのシステム内での対象DBの名称となります。  
今回は慣習に倣い`default`としましたが`twitter_clone`や`master`, `slave`のようにすることも可能です。  

`url`の中で`db:3306`という箇所がありますが、この`db`はネットワーク上でのhost名になります。  
`docker-compose.yaml`でDB側のコンテナのコンテナ名を`db`としているので、そのコンテナ名で対象のサーバへアクセスをしています。

<a id="markdown-migration用のsqlを作成" name="migration用のsqlを作成"></a>
#### Migration用のsqlを作成

DBへの接続設定ができたので、次はDBに対して実行するSQLを用意します。  
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

TODO: sqlの説明

<a id="markdown-evolutionsを実行" name="evolutionsを実行"></a>
#### evolutionsを実行

evolutionsでのマイグレーション実行をするために`play-scala`側のコンテナにアクセスし、サーバを起動してください。

```sh
$ docker-compose exec play-scala bash
/source# sbt run
```

サーバが起動できたらブラウザからPlayへ[アクセス](http://localhost:9000)します。

そうすると以下の画面が表示されると思います。  
  
![evolutions_execute_view](https://github.com/Christina-Inching-Triceps/scala-play_handson/blob/master/lesson2/documents/images/evolutions_execute.png?raw=tru)  

ここで`Apply this script now!`からevolutionsの実行を許可して、マイグレーションを走らせることができます。  
実行をしてみたら、mysql側のコンテナへアクセスして動作を確認してみましょう。  

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

slick-codegeはコンパイル時の自動実行とsbt commandに登録しての手動実行など、いくつかの実行方法があります。  
今回はsbt taskとして登録して手動実行できるように設定してきますが、sbtについては詳しく知らないため、設定方法のみ記述しますが詳細については割愛させていただきます。  

<a id="markdown-sbt-taskの作成" name="sbt-taskの作成"></a>
#### sbt taskの作成

まずは以下のコードを`build.sbt`へ追加します。  

```scala
// add code generation task
lazy val slickCodeGen = taskKey[Unit]("execute Slick CodeGen")
slickCodeGen         := (runMain in Compile).toTask(" com.example.SlickCodeGen").value
```

1行目で`slickCodeGen`という名前でコマンド(Task)を登録しています。  
2行目ではそのタスク名に対して、特定のクラス処理を登録するようなことをしています。※ 詳細は理解できていません。

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

```scala
// -- app/tasks/SlickCodeGen.scala
// Taskに登録したものと同様にpackageを指定
package com.example

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator

object SlickCodeGen extends App {
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

  // slick-codegenを実行
  SourceCodeGenerator.main(
    Array(profile, driver, url, outputDir, pkg, user, password)
  )
}
```

ここで`TypesafeConfig`というライブラリを利用しています。  
これは外部のライブラリになるため`build.sbt`へ依存関係を追加します。  

```scala
// -- build.sbt
"com.typesafe" % "config" % "1.4.0"
```

依存関係を追加したら、新しく追加した設定を読み込めるように`application.conf`へ追記をしていきます。

```
slick {
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

application {
  package = "com.example"
}
```
slick部分の構造が少し変更されているので気をつけてください。  

<a id="markdown-補足" name="補足"></a>
##### 補足

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
    posted_at  DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    created_at DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
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

```scala
// -- CustomSlickCodeGen.scala
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

object CustomSlickCodeGen extends App {
  //.. 変数定義部分省略 (SlickCodeGenと同様)

  // --------------------
  // Create DB Instance
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
##### 日付型の型を変更

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
/** Database column posted_at SqlType(DATETIME) */
val postedAt: Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
/** Database column created_at SqlType(DATETIME) */
val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
/** Database column updated_at SqlType(DATETIME) */
val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
```

ちゃんとLocalDateTimeになっていますね。  

長くなってしまいましたが、これでslick-codegen側の設定は一旦完了になります。

<a id="markdown-play-slickを利用してモデルの操作を行う" name="play-slickを利用してモデルの操作を行う"></a>
### play-slickを利用してモデルの操作を行う



<a id="markdown-tips" name="tips"></a>
## Tips

- db名変更をした場合などは`docker/db/mysql_data/*`を削除してからコンテナの再起動をする
