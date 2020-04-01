#!/bin/bash

# 最終的な成果物を出力する
outputDir=./codelabs
# ビルド用の一時ファイルの格納フォルダ
tmpDir=./tmp

build() {
  if [ ! -e $outputDir ]; then 
    mkdir -p $outputDir
  fi

  if [ ! -e $tmpDir ]; then 
    mkdir -p $tmpDir
  fi

  # mdファイルを全てマージ
  cat $1/*.md > $tmpDir/mergedMd.md
  # 画像をコピー
  cp -pr $1/images $tmpDir/
  # htmlを生成
  claat export -o $outputDir/ $tmpDir/mergedMd.md
  # 不要になったtmpファイルを削除
  rm -rf $tmpDir
}

# ビルド対象markdownファイルが存在するディレクトリを指定して利用する
build $1
