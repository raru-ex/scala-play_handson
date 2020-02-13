# Lesson 0

ハンズオンで開発を行うための環境をセットアップします。  
本リポジトリをCloneして利用する際には、このLessonは不要です。  
個人で1からプロジェクトを作成する際に参考にしていただければと思います。  

## Playframeworkの実行環境のセットアップ

### Dockerのインストール
環境はDockerを利用してセットアップを行うため、Dockerがインストールされていない場合にはDockerのインストールを行ってください。  
Windows: [Download](https://docs.docker.com/docker-for-windows/install/)  
Mac:     [Download](https://docs.docker.com/docker-for-mac/install/)  

### Playframeworkの初期プロジェクトのダウンロード
sbtコマンドでのセットアップも可能ですが、sbtがインストールされてないケースもあるため今回はLightbend社のサイトからダウンロードを行ってください。  
[Download](https://developer.lightbend.com/start/?group=play&project=play-samples-play-scala-hello-world-tutorial)  

### プロジェクトの配置
ダウンロードしたファイルを展開し、プロジェクトのルートに配置します。  
今回はproject_rootフォルダを作成済みのため、ダウンロードファイルが展開された後の中身だけを配置します。  

### DockerでPlay-Scalaの実行環境をセットアップ
project_root直下にdocker-compose.yamlファイルを作成  
以下の内容をdocker-composeファイルに貼り付け  

```yml
# docker-composeの構造のバージョンを指定 (現時点の最新)
version: '3'
# dockerで利用したい各コンテナ(service)をまとめる要素
services:
  # play-scalaという名前をつけて、Serviceを設定。このコンテナはplayを動作させるためのコンテナになります。
  play-scala:
    # 利用するimageを指定。今回はjava8系で動作するsbtの最新imageを指定しています
    image: hseeberger/scala-sbt:8u242_1.3.8_2.13.1
    # 特にこだわりはないので、service名とcontainer名を同じにしています。
    container_name: play-scala
    # playのデフォルト利用ポートが9000番なので9000を指定。hostからも9000でアクセスできるように設定しています。
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

コンテナの起動ができたら、実際にplayを起動してみましょう  

```sh
# play-scalaのコンテナにはbashが入っているので、bashコンソールでアクセスします。
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

以下の画面が表示されれば起動は成功です  
![play hello world](https://github.com/Christina-Inching-Triceps/scala-play_handson/blob/images/documents/images/lesson0/play%E8%B5%B7%E5%8B%95.png?raw=true&s=750)  

## DBのセットアップ

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

最終的に以下のようになっていればOKです

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


## Playframeworkのセットアップ

### build.sbtに依存関係を追加

今回はplay-slickやslick-codegenを利用して環境を作成していきます。  
以下の依存関関係を`build.sbt`へ追加してください。  

```scala
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
      "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
      "com.typesafe.play"      %% "play-slick"            % "5.0.0",
      "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
      "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
      "mysql"                   % "mysql-connector-java"  % "8.0.19",

    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
```

### slick-evolutionsの設定

TODO: slick-evolutionsの説明

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

##### 設定の補足

`slick.dbs.{db_name}.db` というような構造になっており、`db_name`の部分は任意に設定可能です。  
ここで設定した`db_name`がこのシステム内での対象DBの名称となります。  
今回は慣習に倣い`default`としましたが`twitter_clone`や`master`, `slave`のようにすることも可能です。  

`url`の中で`db:3306`という箇所がありますが、この`db`はネットワーク上でのhost名になります。  
`docker-compose.yaml`でDB側のコンテナのコンテナ名を`db`としているので、そのコンテナ名で対象のサーバへアクセスをしています。

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

#### evolutionsを実行

evolutionsでのマイグレーション実行をするために`play-scala`側のコンテナにアクセスし、サーバを起動してください。

```sh
$ docker-compose exec play-scala bash
/source# sbt run
```

サーバが起動できたらブラウザからPlayへ[アクセス](http://localhost:9000)します。

そうすると以下の画面が表示されると思います。  
  
![evolutions_execute_view](https://github.com/Christina-Inching-Triceps/scala-play_handson/blob/master/documents/images/lesson0/evolutions_execute.png?raw=true)  

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




## Tips

- db名変更をした場合などは`docker/db/mysql_data/*`を削除してからコンテナの再起動をする