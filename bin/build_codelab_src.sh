#!/bin/bash

# 最終的な成果物を出力する
outputDir=./codelabs
# ビルド用の一時ファイルの格納フォルダ
tmpDir=./tmp
# 対象外ファイル
ignoreFile=README.md

build() {
  if [ ! -e $outputDir ]; then 
    mkdir -p $outputDir
  fi

  if [ ! -e $tmpDir ]; then 
    mkdir -p $tmpDir
  fi

  # mdファイルを全てマージ
  for file in $1/*.md; do
    if [ $ignoreFile != ${file##*/} ]; then
      cat $file >> $tmpDir/mergedMd.md
    fi
  done

  # 画像をコピー
  cp -pr $1/images $tmpDir/
  # htmlを生成
  claat export -o $outputDir/ $tmpDir/mergedMd.md
  # 不要になったtmpファイルを削除
  rm -rf $tmpDir
}

# ビルド対象markdownファイルが存在するディレクトリを指定して利用する
build $1
