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
