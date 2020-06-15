# READ ME

Scala-Playframeworkの学習をするためのハンズオン資料です。  
各レッスンごとにhandsonフォルダが用意されているため、そのフォルダを利用して開発を進めていただけるようになっています。  

## requirements

### lesson1

- sbt1.3.x

### lesson2

- Docker

## versions

- Playframework 2.8.x
- MySQL 5.7.x
- Slick 3.3.x

## Directory

```sh
├── README.md
├── lesson1
│   ├── documents
│   ├── example
│   └── handson
└── lesson2
    ├── documents
    ├── example
    └── handson
...
```

- documents
  - handson documents.(markdown file)
- example
  - Source code when all hands-on is completed.
- handson
  - handson folder.

## build

binフォルダ以下にはビルド用のスクリプトが配置されています。  
以下のようなコマンドを実行することMarkdownファイルをCodelabs形式のファイルとして出力することができます。  

```sh
$ ./bin/build_codelab_src.sh lesson1/documents
$ ./bin/build_codelab_src.sh lesson2/documents
$ ./bin/build_codelab_src.sh lesson2.5/documents
```

出力されたファイルはリポジトリ直下の `/codelabs` へ出力されます。  

## License

<a rel="license" href="https://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="クリエイティブ・コモンズ・ライセンス" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br />この 作品 は <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">クリエイティブ・コモンズ 表示 - 非営利 - 継承 4.0 国際 ライセンス</a>の下に提供されています。

