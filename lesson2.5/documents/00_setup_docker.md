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

RUN npm install -g @angular/cli
```

nodeのdocker imageはデフォルトのシェルがdashのためbashをインストールしておきます。  
これは今後作成するシェルスクリプトの動作を安定させるためになります。  

その他にはngコマンドを利用するためのangular/cliの導入を行っています。  

次はdocker-composeファイルの変更です。  

`docker-compose.yaml`
```yml
  play_angular:
    build: ./docker/angular
    ports:
      - "5555:5555"
    container_name: play_angular
    volumes:
      - ./:/source
    working_dir: /source/ui
    tty: true
```

docker-composeファイルは今回追加した一部のみを追加しています。  
angualr-elementsを利用するための環境のため、networkの設定は行っていません。  

volumesの設定を `/source/ui/angular-elements` とすることも考えましたが、buildしたangylar-elementsのファイルをPlay側へ送るためにプロジェクトルートからマウントするようにしています。  

## Angular Elementsプロジェクトのセットアップ

作業用の環境が構築できたので、本題のAngular-Elementsをセットアップしていきます。  

### Angularプロジェクトの作成

まずはAngularプロジェクトの作成を行います。  

```sh
$ cd {projec_root}
$ docker-compose up -d
# docker containerへアクセス
$ docker-compose exec play_angular bash

# angular-cliを利用してプロジェクトを新規に作成
root@2156f87e6c03:/source# ng new angular-elements
? Would you like to add Angular routing? Yes
? Which stylesheet format would you like to use? SCSS   [ https://sass-lang.com/documentation/syntax#scss                ]
... 省略 ...


$ cd angular-elements
```

AngularElementsを利用する場合routerはあまり使いませんが、使うこともできるのでインストールしてみています。  
CSSについてはお好みで選択いただいて問題ありません。  

### Angularのプラグインを追加

プロジェクトが作成できたので、最低限必要な設定を行っていきます。  
まずは利用するプラグインのインストールです。  

```sh
# 最終的にhtml, jsにビルドされるためdevelop用としてインストール
$ yarn add -D pug pug-cli typescript@3.8.3

# angular-elementsを利用するためにelementsをインストール
$ yarn add @angular/cdk @angular/elements

# 各々の趣味に合わせて利用するものをインストール
$ yarn add @angular/material @ngrx/store
```

最低限必要なものは `@angular/elements` のみになります。  
他はお好みに合わせてインストールしてください。  


### package.jsonの設定

プラグインがインストールできたら、開発やビルドを行うためにpackage.jsonを設定していきます。  

`ui/angular-elements/package.json`
```json
{
  "name": "angular-elements",
  "version": "0.0.0",
  "scripts": {
    "ng": "ng",
    "build:pug": "pug src/app --doctype html ---pretty ",
    "watch:pug": "pug src/app --doctype html --watch --pretty ",
    "start": "yarn run watch:pug & ng serve --host=0.0.0.0 --port=5555",
    "build": "ng build",
    "build:elements": "ng build --prod --output-hashing=none",
    "test": "ng test",
    "lint": "ng lint",
    "e2e": "ng e2e"
  },
... 省略
```

今回追加したのはそれぞれ以下のような設定です。  
```
build:pug -> pugファイルをhtmlへ変換し出力する
watch:pug -> pugファイルの変更を監視して変更されたときにhtmlへ変換する
statt     -> pugファイルを監視しながらng serveを起動する
※ Dockerコンテナの外から開発用サーバにアクセスできるようにするため`--host=0.0.0.0`設定を追加しています。  

build:elements -> angular-elementsのファイルをprod設定でビルドする
```

`build:elements`でhash値をnoneにしているのは、実際にデプロイするときにはハッシュ値の付与をPlay側で行えば良いからです。

これで開発に必要な最低限の設定が完了です。  

### tsconfig.jsonの修正

次にtsconfigの修正を行います。  

`ui/angular-elements/package.json`
```json
  ... 省略
  "angularCompilerOptions": {
    "fullTemplateTypeCheck": true,
    "strictInjectionParameters": true,
    "enableIvy": false
  }
```

今回コンパイルオプションに1つ設定を追加しています。  

Angular9からivyというレンダリングエンジンが導入されているのですが、それがあることによってmaterialあたりでビルドが落ちてしまいました。  
そのためIvyをfalseに設定しています。  

## angular-elementsのサンプル実装

設定が完了したので早速angular-elementsを利用したサンプルを作成していきます。  

### カスタムエレメントとして利用するComponentを作成

今回は`@Input()`として受け取った文字列をそのまま表示する、シンプルなComponentを作成します。  

`ui/angular-elements/src/app/sample/hello-elements.ts`
```typescript
import { Component, Input }  from '@angular/core';

@Component({
  selector:    'hello-elements',
  templateUrl: './hello-elements.html',
  styleUrls:  ['./hello-elements.scss']
})
export class HelloElementsComponent {
  @Input() displayText: string = 'default text'

  constructor(){}
}
```

`ui/angular-elements/src/app/sample/hello-elements.pug`
```pug
p.custom
  | {{ displayText }}
```

`ui/angular-elements/src/app/sample/hello-elements.scss`
```scss
:host{
  .custom {
    color: red;
  }
}
```

`ui/angular-elements/src/app/sample/index.ts`
```scss
export * from './hello-elements'
```

特に複雑な部分もない実装ですね。  


### 作成したComponetをCustomElementsとして登録

Componentが作成できたので、CustomElementsとして利用できるように登録していきます。  

まずは`app.module.ts`に登録します。  

`ui/angular-elements/src/app/app.module.ts`
```typescript
import { BrowserModule }          from '@angular/platform-browser';
import { NgModule }               from '@angular/core';
import { AppComponent }           from './app.component';
import { HelloElementsComponent } from 'src/app/sample';

@NgModule({
  declarations: [
    AppComponent,
    HelloElementsComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [HelloElementsComponent]
})
export class AppModule { }
```

先ほど作成してComponentsを`declarations`と`entryComponents`に追加しています。  
またimportsに登録されていた`AppRoutingModule`は、今のところ利用しないため削除しています。  

次はAngularに読み込ませたComponentsをCustomElementsとして登録していきます。  

`ui/angular-elements/src/app/app.component.ts`
```
import { Component, Injector }    from '@angular/core';
import { createCustomElement }    from '@angular/elements'
import { HelloElementsComponent } from 'src/app/sample';

@Component({
  selector: 'app-root',
  template: ''
})
export class AppComponent {
  constructor(injector: Injector) {
    customElements.define('hello-elements', createCustomElement(HelloElementsComponent, {injector}))
  }
}
```

AppComponentのコンストラクタでカスタムエレメントの定義をしています。  
ここで定義(define)するカスタムエレメントの名前はComponentのselectorと揃える必要があることに注意してください。  

これで作成したComponentの登録は完了です。  

## ビルド設定とPlayへの登録

AngularElementsのファイルが作成できたので、これをビルドしてPlayから呼んでみましょう。  

### Build用シェルの作成

まずはプロジェクトをビルドする必要があるので、ビルド設定を追加していきます。  
毎回手動でビルドするのは面倒なので、シェルスクリプトを作成して対応していきましょう。  

`bin/build_elements.sh`
```sh
#!/bin/bash

declare -a targetVersions=(es5 es2015)
angularProjectDir=./ui/angular-elements
angularProjectOutputDir=$angularProjectDir/dist/angular-elements

build() {
  # angularのファイルをbuild
  cd $angularProjectDir
  yarn run build:elements
  cd -

  # build versionごとにファイルを作成する
  for esVer in ${targetVersions[@]}; do
    # ビルドされたファイルを追記するようになっているため、一度ファイルを空にする
    cat /dev/null > $angularProjectOutputDir/app-angular-${esVer}.js

    # buildされたファイルを順次処理
    for file in `find $angularProjectOutputDir -maxdepth 1 -type f -name \*"$esVer"\* `; do
      # app-angular自身を対象から外すためチェック
      if [ ! `echo ${file} | grep app-angular` ]; then
        # 文末で改行しつつファイルを結合
        cat $file >> $angularProjectOutputDir/app-angular-${esVer}.js
        echo "" >> $angularProjectOutputDir/app-angular-${esVer}.js
      fi
    done
  done
}

build
```

angularではes5, es2015用のファイルが出力されるため、それぞれ1つのファイルにまとめるようにビルドしています。  
node用のubuntusイメージでは`#!/bin/sh`とするとエイリアスされているdashでシェルが起動してしまうため、別途インストールしていたbashを利用するようにしています。  
dashだとarraryが上手く使えなかったのですよね...  

これでプロジェクトのビルドができるようになったので、次はこれらのファイルをPlayへ配置するシェルを作成します。  

`bin/build_elements_to_play.sh`
```sh
#!/bin/bash

angularProjectDir=./ui/angular-elements
angularProjectOutputDir=$angularProjectDir/dist/angular-elements
buildFilePrefix=app-angular
buildCssFile=styles.css
jsDir=public/javascripts
cssDir=public/stylesheets

copy_to_play() {
  # js
  for file in `find $angularProjectOutputDir -maxdepth 1 -type f -name \*"$buildFilePrefix"\* `; do
    cp -f $file $jsDir
  done

  # css
  for file in `find $angularProjectOutputDir -maxdepth 1 -type f -name \*"$buildCssFile"\* `; do
    cp -f $file $cssDir/elements.css
  done

}

/bin/bash ./bin/build_elements.sh
copy_to_play
```

通常利用する際には`build_elements_to_play`だけを実行すれば良いように先ほど作成したビルド用のシェルを実行しつつ、ファイルをPlayのPublicフォルダにコピーしています。  

それぞれあまり綺麗なシェルスクリプトではないのですが、動けば良いと思います。  

### Play側でファイルの読み込み

作成したファイルが配置されたのでPlayから呼び出してみます。  

`app/views/main.scala.html`
```html
@* js, cssを受け取れるように引数を追加。渡さなくてもいいようにデフォルト値も設定 *@
@(
  title:  String,
  script: Html = Html(""),
  css:    Html = Html(""),
)(content: Html)

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>@title</title>
        @* resetのcssを適用 *@
        <link href="https://unpkg.com/sanitize.css" rel="stylesheet"/>
        @* NotoSansとfont-awesomeを追加 *@
        <link rel="stylesheet" href="https://fonts.googleapis.com/earlyaccess/notosansjapanese.css">
        <link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
        <link rel="stylesheet" media="screen" href="@routes.Assets.versioned("stylesheets/main.css")">
        @* angular-elementsのcss読み込み *@
        <link rel="stylesheet" media="screen" href="@routes.Assets.versioned("stylesheets/elements.css")">
        <link rel="shortcut icon" type="image/png" href="@routes.Assets.versioned("images/favicon.png")">
        @* 引数の展開 *@
        @css
    </head>
    <body>
      @* angular-elementsのComponentを呼び出し *@
      <app-root></app-root>
      <hello-elements display-text="angular elements">
      </hello-elements>

      @content

      <script src="@routes.Assets.versioned("javascripts/main.js")" type="text/javascript"></script>
      @* angular-elementsのjs読み込み *@
      <script src="@routes.Assets.versioned("javascripts/app-angular-es5.js")" type="text/javascript"></script>

      @* 引数の展開 *@
      @script
    </body>
</html>
```

コメントにも追加していますがhead内でcssの読み込み。  
bodyの下部でjavascriptの読み込みをしています。  
またbody内で`app-root`, `hello-elements`を呼び出しています。  

カスタムエレメントとしてAngularのComponentを呼び出すときには@Inputに指定されている属性はハイフン繋ぎのケバブケースで呼び出す必要があることに注意してください。  

これでPlayをsbt runして赤文字の`angular elements`というテキストが表示されていれば成功です。  


