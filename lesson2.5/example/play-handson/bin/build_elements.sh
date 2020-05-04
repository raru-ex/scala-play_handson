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
