package models

import java.time.LocalDateTime
import slick.jdbc.{GetResult}
import java.time.format.DateTimeFormatter

/* def *のtupleでマッピングをするサンプル実装 */
object MyProfileTweetTable extends {
  val profile    = MyDBProfile
} with MyProfileTweetTable

trait MyProfileTweetTable {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[Tweet] = GetResult{
    prs => import prs._
    Tweet.tupled((
      <<[Option[Long]],
      <<[String],
      <<[LocalDateTime],
      <<[LocalDateTime],
      <<[LocalDateTime]
    ))
  }

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  class MyProfileTweetTable(_tableTag: Tag) extends profile.api.Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {
    def * = (id, content, postedAt, createdAt, updatedAt) <> (
      (x: (Long, String, LocalDateTime, LocalDateTime, LocalDateTime)) => {
        Tweet(
          Some(x._1),
          x._2,
          x._3,
          x._4,
          x._5
        )
      },
      (tweet: Tweet) => {
        Some((tweet.id.getOrElse(0L), tweet.content, tweet.postedAt, tweet.createdAt, tweet.updatedAt))
      }
    )

    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((Option(_1.get), _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
  }

  lazy val query = new TableQuery(tag => new MyProfileTweetTable(tag))
}
