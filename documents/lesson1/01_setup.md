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
    - [前提](#前提)
    - [推奨環境](#推奨環境)
    - [Playの導入](#playの導入)

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

Skinny Frameworkは私がScalaで初めて利用していたフレームワークのため、個人的な思い入れが深いです。  
(実はLiftというフレームワークを10年くらい前に利用したことがありますが...)

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

<a id="markdown-前提" name="前提"></a>
## 前提

本ハンズオンは以下を前提として構築されています。  
前提となっているツールがインストールされていない場合には、各自の環境に合わせてインストールを行ってください。  

- マシンにsbtがインストールされている

[Windows](https://www.scala-sbt.org/1.x/docs/ja/Installing-sbt-on-Windows.html)
[Mac](https://www.scala-sbt.org/1.x/docs/ja/Installing-sbt-on-Mac.html)
[Linux](https://www.scala-sbt.org/1.x/docs/ja/Installing-sbt-on-Linux.html)

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

これでコマンドを実行したフォルダ以下に`name`で指定したプロジェクト名のフォルダが作成されています。  

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


