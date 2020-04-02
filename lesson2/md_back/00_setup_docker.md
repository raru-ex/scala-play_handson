<a id="markdown-目次" name="目次"></a>
# 目次

<!-- TOC -->

- [目次](#目次)
- [Lesson2 Dockerセットアップ](#lesson2-dockerセットアップ)
    - [Lesson1のプロジェクトのコピー](#lesson1のプロジェクトのコピー)
    - [Dockerのインストール](#dockerのインストール)
    - [DockerでPlay-Scalaの実行環境をセットアップ](#dockerでplay-scalaの実行環境をセットアップ)
    - [DockerでDBのセットアップ](#dockerでdbのセットアップ)
        - [Dockerfileの設定](#dockerfileの設定)
        - [my.cnfを配置](#mycnfを配置)
        - [docker-composeにDBの設定を追記](#docker-composeにdbの設定を追記)
    - [Tips](#tips)

<!-- /TOC -->

<a id="markdown-lesson2-dockerセットアップ" name="lesson2-dockerセットアップ"></a>
# Lesson2 Dockerセットアップ

ハンズオンで開発を行うための環境をセットアップします。  
Dockerを利用して環境を作成しているため、手順兼説明資料として本章を残します。  
ハンズオン用のプロジェクトにはDocker設定が完了している状態のものを配置しているので、この章はご自身で対応していただく必要はありません。  
今後自身でプロジェクトを作成する際や、ハンズオン用プロジェクトの構成が気になる人は参考にしていただければと思います。  

<a id="markdown-lesson1のプロジェクトのコピー" name="lesson1のプロジェクトのコピー"></a>
## Lesson1のプロジェクトのコピー

まずはLesson1で作成したプロジェクトをコピーします。  
これはコンソールからでもFinderからでも構いません。  

```sh
$ cd {repository_root}
$ cp -rp lesson1/example/play-handson lesson2/handson/

# 以後このプロジェクトルートで作業を行います。
$ cd lesson2/handson/play-handson
```

<a id="markdown-dockerのインストール" name="dockerのインストール"></a>
## Dockerのインストール

環境はDockerを利用してセットアップを行うため、Dockerがインストールされていない場合にはDockerのインストールを行ってください。  

Windows: [Download](https://docs.docker.com/docker-for-windows/install/)  
Mac:     [Download](https://docs.docker.com/docker-for-mac/install/)  

<a id="markdown-dockerでplay-scalaの実行環境をセットアップ" name="dockerでplay-scalaの実行環境をセットアップ"></a>
## DockerでPlay-Scalaの実行環境をセットアップ

project_root直下にdocker-compose.yamlファイルを作成  
以下の内容をdocker-composeファイルに貼り付け  

`docker-compose.yaml`
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
[play-handson] $ run
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
<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/lesson2/documents/images/01_docker_play_init.png" width="450">

<a id="markdown-dockerでdbのセットアップ" name="dockerでdbのセットアップ"></a>
## DockerでDBのセットアップ

MySQLを利用するDBのdocker-compose設定を行なっていきます。  
`project_root` 直下に以下のようにディレクトリ/ファイルを作成してください。  
`init.sql` を作成していますが、現状利用しないため中身は空のままとしておいてください。  

```sh
docker
└── db
    ├── Dockerfile
    ├── init
    │   └── init.sql
    ├── my.cnf
    └── mysql_data/
```

<a id="markdown-dockerfileの設定" name="dockerfileの設定"></a>
### Dockerfileの設定

`doker/db/Dockerfile` を編集していきます。  
以下のようにファイルに記載してください。  

`/docker/db/Dockerfile`
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
### my.cnfを配置

先ほどのtreeの設定と同様の箇所にmy.cnfファイルを配置します。  
以下のようにファイルに記載してください。  

`/docker/db/my.cnf`
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
### docker-composeにDBの設定を追記

`docker-compose.yaml` にDBのService設定を追加します。  
以下の設定をファイルに追加してください。  
今回は`twitter_clone`という名前のDBを作るようにしています。  

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
      # Container内にデータベースを作成
      MYSQL_DATABASE: twitter_clone
    networks:
      - app-net
```

最終的に以下のようになっていればOKです。  

`/docker-compose.yaml`
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
      # DB名
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
正しく設定されているかMySQLにアクセスして検証してみましょう。  

```sh
$ docker-compose exec db bash
root@703885e52d0c:/# mysql -u root -proot
# ... 省略 ...

mysql>
```

このようにmysqlにアクセスができていれば成功です。  


<a id="markdown-tips" name="tips"></a>
## Tips

- db名変更をした場合などは`docker/db/mysql_data/*`を削除してからコンテナの再起動をする
