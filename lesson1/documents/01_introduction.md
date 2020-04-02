summary: Play Framework Handson Lesson1
id: play-handson-lesson1
enviroments: web
status: Draft
feedback link: https://github.com/Christina-Inching-Triceps/scala-play_handson/issues
tags: scala,Play Framework
authors: chiristina.inching.triceps

# Play Framewworkハンズオン 1章
Lesson1ではPlayを利用して、簡単なCRUDを作成していきます。  
DB接続やその他周辺技術を利用すると複雑度が増してしまうため、この章ではDBは利用せずに機能を実装していきます。  
まずはPlay Frameworkをシンプルな状態のまま利用して、Play Frameworkに慣れていきましょう。  
  
またScala-Playで開発を行うにあたり、必要となる基本的な知識・言葉についても非常に簡単にではありますが補足させていただきます。  
既にご存知の内容であれば、基本情報のブロックはスキップしていただいて問題ございません。  

## 基本情報

### sbtとは

主にScalaで利用されているビルドツールです。  
設定ファイルの記述はScalaのDSLを用いて行います。  

ライブラリの依存関係の解決や、デプロイのためのパッケージング、インクリメンタルなテストやコンパイルの実行などができます。  
この後利用しますが、他にもシードからのプロジェクト生成などを行うことなども可能です。  

scalacコマンドなどを利用してscalaプログラムの実行を行うこともできますが、一般的にはsbtを利用してその上でScalaを利用することが多いと思います。  

参照: [Wikipedia](https://ja.wikipedia.org/wiki/Sbt)

### Play Frameworkとは

元々はJava向けのWebフレームワークです。  
2010年11月にリリースされたPlay 1.1からはScalaをサポートしており、現在広く利用されているフレームワークの一つです。  

MVCアーキテクチャに親和性が高く、Web Applicationに必要な一通りの機能が網羅されています。  
ScalaでのWeb開発では利用率が高く、参考情報も豊富なため今回のハンズオンに採用しています。  
ちなみにPlay2.3のころから後述のAkkaを統合しています。  

[参照]  
[Play Framework](https://ja.wikipedia.org/wiki/Play_Framework)  
[JetBrains: どのフレームワーク / ライブラリをウェブ開発に定期的に使用していますか？](https://www.jetbrains.com/ja-jp/lp/devecosystem-2019/scala/)  

#### 他のScala WEB Framework

その他の主要なフレームワークについても一部ご紹介しておきます。  

- [Scalatra](https://scalatra.org/)
  - Gitbucket(takezoeさん作成)で利用されているフレームワークです
  - RubyのフレームワークであるSinatraに影響を受けています
- [Skinny Framework](http://skinny-framework.org/)
  - seratchさんの作成してるフレームワークです
  - Ruby on Railsにインスパイアされ、作成されています
- [Akka](https://en.wikipedia.org/wiki/Akka_(toolkit))
  - Play Frameworkの内部でも利用されているライブラリ
  - 公式ではオープソースツールキットおよびランタイムと記載されており、フレームワークではない
  - Sprayの後継として位置付けられていて、Akka○○という形でいくつかのライブラリに分かれている
  - Akka httpが一番よく聞く。Http関連のモジュール
  - Actor modelを採用している -> [Wikipedia](https://ja.wikipedia.org/wiki/%E3%82%A2%E3%82%AF%E3%82%BF%E3%83%BC%E3%83%A2%E3%83%87%E3%83%AB)


### オススメの開発エディタ

- IntelliJ IDEA
- Visual Studio Code & metals
- Vim or Emacs & metals

IDEについてはほぼほぼIntellij IDEAで統一されている印象です。  
Community EditionでもScala Pluginを導入すれば、個人で開発する範囲であればあまり不自由なく開発が行えると思います。  

他の選択肢として、最近はMetalsというScala向けのLanguage Serverの開発が進んでおり、LSPを利用した連携機能を持つエディタであればかなり快適に開発を行うことも可能です。  
事実、私はVimとMetalsを利用して開発を行っています。  

個人的には、強いこだわりがない限りは`IntelliJ IDEA`の利用をオススメします。  


