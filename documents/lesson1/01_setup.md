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
    - [詳細ページ作成](#詳細ページ作成)
    - [登録・更新ページ作成](#登録・更新ページ作成)
    - [Twirlの共通コンポーネント作成](#twirlの共通コンポーネント作成)

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

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/01_play_hello_world.png" witdh="450">

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

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/02_JetBrains_top.png" witdh="450">

各々OSに合わせたダウンロードページへ遷移します。(たぶん)  
ダウンロードページに遷移したら、以下のボタンから無償版のIDEAをダウンロードします。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/03_JetBrains_download_page.png" witdh="450">

IDEAをインストールできたら、IDEAを起動して次のステップへ進んでください。  

<a id="markdown-ideaへのプロジェクトの取り込み" name="ideaへのプロジェクトの取り込み"></a>
### IDEAへのプロジェクトの取り込み

まず、IDEAを起動したら`Open`からプロジェクトを開いていきましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/04_IntelliJ_top.png" witdh="450">

ファインダーから選択するときには、対象のプロジェクトを反転させてから`Open`で問題ありません。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/05_chose_project.png" witdh="450">

次にIDEAのテーマを選択します。  
ここはお好みのテーマを選択してください。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/06_select_view_mode.png" witdh="450">

次のステップでは`shell`からIDEAを起動するためにPATHを通すか選択する画面になります。  
ここもご自身で自由に設定いただいて問題ありません。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/07_select_bin_setting.png" witdh="450">

次は好みの設定があるようでしたら変更いただいても問題ありませんが、基本的にはそのままで大丈夫です。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/08_setting1.png" witdh="450">

この画面では印がしていあるように、Scalaのインストールだけは必須で行ってください。  
他のものについては任意です。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/09_install_scala.png" witdh="450">

これでIDEAが起動して、プロジェクトが読み込まれます。  
またそのときに下の画像のようなポップアップが表示されたら`Import Changes`を選択しましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/10_import_sbt.png" witdh="450">

sbtに変更が加わったときなどに表示されます。これをしないとsbt関連の変更が適用されないのでご注意ください。  
  
`Enable Auto-Import`をすると自動で設定を更新するようになりますが、sbtの読み込みはマシンパワーを使うことがあるので私は手動が好きです。  
ただこれはお好みで選んでいただいて構いません。  

では、起動が完了したら試しに`app/controllers/HomeController.scala`のファイルを開いてみましょう。  

<img src="https://raw.githubusercontent.com/Christina-Inching-Triceps/scala-play_handson/master/documents/images/lesson1/11_open_homecontroller.png" witdh="450">

ソースコード上で、21行目の`Action`に`Cmd`キーを押しながらカーソルを移動してみてください。  
マウスが当たったときにカーソルが指マークに変わるとおもいます。  
そこでクリックをしてみましょう。  
Actionの実装に飛ぶことができれば設定は正常にされている状態になっています。  

うまく動作しないときはJDKのインストールとScalaの設定ができていないときがあります。  
そのときは声をかけてください。  

これでIDEAの設定は完了になります。  


<a id="markdown-一覧ページ作成" name="一覧ページ作成"></a>
## 一覧ページ作成

<a id="markdown-詳細ページ作成" name="詳細ページ作成"></a>
## 詳細ページ作成

<a id="markdown-登録・更新ページ作成" name="登録・更新ページ作成"></a>
## 登録・更新ページ作成

<a id="markdown-twirlの共通コンポーネント作成" name="twirlの共通コンポーネント作成"></a>
## Twirlの共通コンポーネント作成