package controllers.tweet

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent
import models.Tweet
import play.api.http.DefaultHttpErrorHandler

/**
  * @SingletonでPlayFrameworkの管理下でSingletonオブジェクトとして本クラスを扱う指定をする
  * @Injectでconstructorの引数をDIする
  * BaseControllerにはprotected の controllerComponentsが存在するため、そこに代入されている。
  * controllerComponentsがActionメソッドを持つため、Actionがコールできる
  *   ActionはcontrollerComponents.actionBuilderと同じ
  */
@Singleton
class TweetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  // DBのMockとして利用したいので、クラスのフィールドとして定義し直す
  val tweets: Seq[Tweet] = (1L to 10L).map(i => Tweet(Some(i), s"test tweet${i.toString}"))

  def list() =  Action { implicit request: Request[AnyContent] =>
    // Ok()はステータスコードが200な、Resultをreturnします。
    // つまり正常系としてviews.html.tweet.listのコンテンツを返すということになります。


    // viewの引数としてtweetsを渡します。
    Ok(views.html.tweet.list(tweets))
  }

  def show(id: Long) = Action { implicit request: Request[AnyContent] =>
    // idが存在して、値が一致する場合にfindが成立
    tweets.find(_.id.exists(_ == id)) match {
      case Some(tweet) => Ok(views.html.tweet.show(tweet))
      case None        => NotFound("this tweet is not found")
    }
  }
}
