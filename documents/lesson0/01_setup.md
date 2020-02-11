# Lesson 0

ハンズオンで開発を行うための環境をセットアップします

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

### プロジェクトに対してのDockerをセットアップ
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

