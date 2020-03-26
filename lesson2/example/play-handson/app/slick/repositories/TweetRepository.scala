package slick.repositories

import java.time.LocalDateTime
import play.api.db.slick.{HasDatabaseConfigProvider,DatabaseConfigProvider}
import javax.inject.{Inject, Singleton}
import slick.jdbc.{JdbcProfile, GetResult}
import scala.concurrent.{Future, ExecutionContext}
import slick.models.Tweet

@Singleton
class TweetRepository @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val tweet = new TableQuery(tag => new TweetTable(tag))

  // ########## [DBIO Methods] ##########

  /**
    * tweetを全件取得
    */
  def all(): Future[Seq[Tweet]] = db.run(tweet.result)

  // sample
  def allOdd(): Future[Seq[Tweet]] = db.run(
    tweet.filter(x => x.id % 2L === 0L).result
  )

  // sample
  def findById(id: Long): Future[Seq[Tweet]] = {
    val db = Database.forConfig("your_db_setting")
    db.run(
      tweet.filter(x => x.id  === id).result
    )
  }


  // ########## [Table Mapping] ##########
  private class TweetTable(_tableTag: Tag) extends Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {

    // Tableとのカラムマッピング
    val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")

    // Plain SQLでデータ取得を行う用のマッピング
    implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[Tweet] = GetResult{
      prs => import prs._
      Tweet.tupled((Some(<<[Long]), <<[String], <<[LocalDateTime], <<[LocalDateTime], <<[LocalDateTime]))
    }

    // model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
    // O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
    def * = (id.?, content, postedAt, createdAt, updatedAt) <> (Tweet.tupled, Tweet.unapply)

    // Maps whole row to an option. Useful for outer joins.
    def ? = ((
      Rep.Some(id),
      Rep.Some(content),
      Rep.Some(postedAt),
      Rep.Some(createdAt),
      Rep.Some(updatedAt)
    )).shaped.<>(
    { r =>
      import r._;
      _1.map( _=>
          Tweet.tupled((
            Some(_1.get), // モデル側はidがOptionなのでOptionで包んでいる
            _2.get,
            _3.get,
            _4.get,
            _5.get
          ))
    )},
    (_:Any) =>
      throw new Exception("Inserting into ? projection not supported.")
    )
  }
}
