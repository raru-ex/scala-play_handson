summary: Play Framework Handson Lesson1
id: play-handson-lesson1
enviroments: web
status: Draft
feedback link: https://github.com/Christina-Inching-Triceps/scala-play_handson/issues
tags: scala,Play Framework
authors: chiristina.inching.triceps

# Play Framewworkハンズオン 1章

## 基本情報

本ハンズオンではTwitterを模倣したシステムを構築しながらPlay Frameworkの使い方を学んでいきます。  

Lesson1ではPlayを利用して、簡単なCRUDを作成していきます。  
最初はPlayに集中するためにDB接続やその他周辺技術には触れません。  
まずはPlay Frameworkをシンプルな状態のまま利用して、Play Frameworkに慣れていきましょう。  
  
またScala-Playで開発を行うにあたり必要となる基本的な知識・言葉については、非常に簡単にではありますが補足していきます。  
既にご存知の内容であれば、基本情報のページはスキップしていただいて問題ありません。  


### SBTとは

SBTはScalaの考案者が創設したLightbend社が作成しているビルドツールです。  
ビルドツールというのは自分が書いたプログラムを実際にパソコン上で動作させるために必要な手順を支援してくれるツールです。  

sbtを利用するとライブラリの依存関係の解決や、デプロイのためのパッケージング、インクリメンタルなテストやコンパイルの実行を簡単に行うことができるようになります。  
scalacコマンドなどを利用してscalaプログラムの実行を行うこともできますが、一般的にはsbtを利用してその上でScalaを利用することが多いと思います。  

この後利用しますが、他にもシードからのプロジェクト生成などを行うことなども可能です。  

Javaで利用されているAnt, Mavenを利用することも可能ですが、scalaではsbtが利用されることが一般的なのでこちらをお勧めします。  

参照: [Wikipedia](https://ja.wikipedia.org/wiki/Sbt)

### Play Frameworkとは

元々はJava向けに開発されたWebフレームワークです。  
2010年11月にリリースされたPlay 1.1からはScalaをサポートしており、現在広く利用されています。  

MVCアーキテクチャに親和性が高く、Webアプリケーション開発に必要な一通りの機能が網羅されています。  
ScalaでのWeb開発では採用率が高く参考情報も豊富なため、今回のハンズオンに採用しています。  
ちなみにPlay2.3のころから後述のAkkaを統合しています。  

[参照]  
[Play Framework](https://ja.wikipedia.org/wiki/Play_Framework)  
[JetBrains: どのフレームワーク / ライブラリをウェブ開発に定期的に使用していますか？](https://www.jetbrains.com/ja-jp/lp/devecosystem-2019/scala/)  

#### 他のScala WEB Framework

その他の主要なフレームワークについても一部ご紹介しておきます。  

- [Scalatra](https://scalatra.org/)
  - takezoeさん作成のGitbucketで利用されているフレームワークです
  - RubyのフレームワークであるSinatraに影響を受けています
- [Skinny Framework](http://skinny-framework.org/)
  - seratchさんの作成してるフレームワークです
  - Ruby on Railsにインスパイアされ作成されています
- [Akka](https://en.wikipedia.org/wiki/Akka_(toolkit))
  - Play Frameworkの内部でも利用されているライブラリです
  - 公式ではオープソースツールキットおよびランタイムと記載されており、フレームワークではありません
  - Sprayの後継として位置付けられていて、Akka○○という形でいくつかのライブラリに分かれています
  - Akka httpが一番よく聞きます。これはHttp関連のモジュールです
  - Actor modelを採用しています -> [Wikipedia](https://ja.wikipedia.org/wiki/%E3%82%A2%E3%82%AF%E3%82%BF%E3%83%BC%E3%83%A2%E3%83%87%E3%83%AB)


### オススメの開発エディタ

- IntelliJ IDEA
- Visual Studio Code & metals
- Vim or Emacs & metals

IDEについてはほぼほぼIntellij IDEAで統一されている印象です。  
Community EditionでもScala Pluginを導入すると、個人開発の範囲であればあまり不自由なく開発が行えると思います。  

他の選択肢としてMetalsというScala向けのLanguage Serverを利用する方法があります。  
最近活発に開発がされており、LSPを利用した連携機能を持つエディタであればかなり快適に開発を行うことも可能です。  
VS Codeが有名ですね。  
ちなみに私はVimとMetalsを利用して開発を行っています。  

個人的には、強いこだわりがない限りは`IntelliJ IDEA`の利用をオススメします。  
IntelliJ IDEAにはScalaのプログラムを「よりScalaらしい」記述にリファクタリングしてくれる機能など、便利な機能が多く用意されているためです。  

