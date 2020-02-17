package com.example.models

import java.time.LocalDateTime
import slick.jdbc.MySQLProfile.api._
import slick.model.ForeignKeyAction
import slick.jdbc.{GetResult => GR}
import slick.lifted.Tag
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

case class Tweet(id: Long, content: String, postedAt: MySQLDateTime, createdAt: MySQLDateTime, updatedAt: MySQLDateTime)

object TweetTable {
  implicit def GetResultTweet(implicit e0: GR[Long], e1: GR[String], e2: GR[MySQLDateTime]): GR[Tweet] = GR{
    prs => import prs._
    Tweet.tupled((<<[Long], <<[String], <<[MySQLDateTime], <<[MySQLDateTime], <<[MySQLDateTime]))
  }

  lazy val TweetQuery = new TableQuery(tag => new TweetTable(tag))
}


case class MySQLDateTime(value: String) {
  def toLocalDateTime: LocalDateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

object MySQLDateTime {
  def apply(time: LocalDateTime): MySQLDateTime = MySQLDateTime(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
}

class TweetTable(_tableTag: Tag) extends Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {
  // mysqlはtextでslickに日付が渡るので変換をかける
  // LocalDatetime, Timestampだとそもそもメソッドが呼ばれない。他のものに処理を奪われてる?? -> 重複するとambiguous implicitになるので、よくわからない
  implicit lazy val timeMapper = MappedColumnType.base[MySQLDateTime, Timestamp]( { l =>
      println("===== foooooooooooo  =====")
      Timestamp.valueOf(l.value)
    },
    { t =>
      println("===== heihei =====")
      println(t.toString)
      MySQLDateTime(t.toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    }
  )


    def * = (id, content, postedAt, createdAt, updatedAt) <> (Tweet.tupled, Tweet.unapply)
    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id:        Rep[Long]          = column[Long]         ("id",      O.AutoInc, O.PrimaryKey)
    val content:   Rep[String]        = column[String]       ("content", O.Length(120,varying=true))
    val postedAt:  Rep[MySQLDateTime] = column[MySQLDateTime]("posted_at")
    val createdAt: Rep[MySQLDateTime] = column[MySQLDateTime]("created_at")
    val updatedAt: Rep[MySQLDateTime] = column[MySQLDateTime]("updated_at")
  }

