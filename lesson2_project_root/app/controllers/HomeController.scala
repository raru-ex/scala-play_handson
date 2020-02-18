package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import com.example.models.TweetTable
import slick.jdbc.MySQLProfile.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)
extends AbstractController(cc)
with HasDatabaseConfigProvider[JdbcProfile] {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action async { implicit request: Request[AnyContent] =>

    for (
      tweets <-  db.run(TweetTable.TweetQuery.sortBy(_.id.desc).result)
    ) yield {
      Ok(views.html.tweet.list(tweets))
    }
  }

  def explore() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.explore())
  }
  
  def tutorial() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.tutorial())
  }
  
}
