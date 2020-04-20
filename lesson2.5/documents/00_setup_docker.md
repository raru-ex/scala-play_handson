summary: Play Framework Handson Lesson2.5
id: play-handson-lesson2.5
enviroments: web
status: Draft
feedback link: https://github.com/Christina-Inching-Triceps/scala-play_handson/issues
tags: scala,Play Framework,Angular
authors: chiristina.inching.triceps

# Play Framewworkハンズオン 2.5章

今回は個人的な興味でPlayFrameworkの環境にAngular-Elementsを導入してきます。  
Vue, Reactがお好みの方やAngular-Elementsが不要な方はスキップしてしまってください。  

## Docker環境のセットアップ

今までと同様にDockerで環境を構築していきます。  

最初にAngular Elements用のフォルダを作成していきます。  
今回はuiというフォルダで作業を進めていきます。  

```sh
$ mkdir ui
```

作業用のフォルダが作成できたので、引き続きDockerfileの作成とdocker-compose.yamlの編集をしていきましょう。  

まず最初にDockerfileです。  

`docker/angular/Dockerfile`
```dockerfile
FROM node:10.16.3

RUN apt-get update
RUN apt-get install -y bash
RUN chsh -s /bin/bash

RUN npm install -g @angular/cli
```

nodeのdocker imageはデフォルトのシェルがcshのためbashに変更しています。  
一部コマンドの挙動が違ってビルド設定がしづらいためです。  

その他にはngコマンドを利用するためのangular/cliの導入を行っています。  

次はdocker-composeです。  

`docker-compose.yaml`
```yml
  play_angular:
    build: ./docker/angular
    ports:
      - "5555:5555"
    container_name: play_angular
    volumes:
      - ./angular:/source
    working_dir: /source
    tty: true
```

docker-composeファイルは今回追加した一部のみを追加しています。  
angualr-elementsを利用するための環境のため、networkの設定は行っていません。  

### Angular Elementsプロジェクトのセットアップ

環境が構築できたので、Angular-Elementsをセットアップしていきます。  

#### Angularプロジェクトの作成

まずはAngularプロジェクトの作成を行います。  

```sh
# docker containerへアクセス
$ docker-compose exec play_angular bash

# angular-cliを利用してプロジェクトを新規に作成
root@2156f87e6c03:/source# ng new angular-elements
? Would you like to add Angular routing? Yes
? Which stylesheet format would you like to use? SCSS   [ https://sass-lang.com/documentation/syntax#scss                ]
```


