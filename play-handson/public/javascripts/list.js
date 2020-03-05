// DOM読み込みが完了してから処理
document.addEventListener("DOMContentLoaded",function(){
  // HTMLCollectionを配列に変換しつつ削除アイコンを取得
  const deleteActions = Array.from(
    document.getElementsByClassName("delete")
  );

  // それぞれのアイコンに削除フォーム実行のonclickイベントを設定
  deleteActions.forEach(action => {
    // eventを取得して、クリックされた要素(target)の親要素であるformをsubmitする
    action.addEventListener("click", (e) => {
      e.target.parentNode.submit();
    });
  });
});
