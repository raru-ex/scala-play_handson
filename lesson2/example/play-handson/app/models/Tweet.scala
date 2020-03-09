package com.example.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import slick.jdbc.MySQLProfile.api._
import slick.model.ForeignKeyAction
import slick.jdbc.{GetResult => GR}
import slick.lifted.Tag

case class Tweet(id: Long, content: String, postedAt: LocalDateTime, createdAt: LocalDateTime, updatedAt: LocalDateTime)

object TweetTable {
  // sql""記法などでas[Tweet]するときにマッピングとして利用される
  implicit def GetResultTweet(implicit e0: GR[Long], e1: GR[String]): GR[Tweet] = GR{
    prs => import prs._
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    Tweet.tupled((<<[Long], <<[String], LocalDateTime.parse(<<[String], format), LocalDateTime.parse(<<[String], format), LocalDateTime.parse(<<[String], format)))
  }

  lazy val TweetQuery = new TableQuery(tag => new TweetTable(tag))
}

class TweetTable(_tableTag: Tag) extends Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {
  // LocalDateTimeにマッピングしようとするときに以下の場所(slick.jdbc.MySQLProfile)で落ちてる。iso8601では少数の桁数を定義してない
  // override def getValue(r: ResultSet, idx: Int) : LocalDateTime = {
  //      r.getString(idx) match {
  //        case null => null
  //        case iso8601String => LocalDateTime.parse(iso8601String)
  //      }
  //    }

  // TODO: S*9をどうにかしたい。Tableのcolumn設定に環境に依存しそう.
  // mysql, psqlが6桁まで、oracle, db2は9桁まで持てそう。そもそもLocalDateTimeの最小桁数が9桁
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")

  def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>(
  {
    r=>import r._; _1.map(_=> Tweet.tupled((
      _1.get,
      _2.get,
      LocalDateTime.parse(_3.get, format),
      LocalDateTime.parse(_4.get, format),
      LocalDateTime.parse(_5.get, format)
    )))
  }, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  def * = (id, content, postedAt, createdAt, updatedAt) <> (
    (x: (Long, String, String, String, String)) => {
      Tweet(x._1, x._2, LocalDateTime.parse(x._3, format), LocalDateTime.parse(x._4, format), LocalDateTime.parse(x._5, format))
    },
    (tweet: Tweet) => {
      Some(tweet.id, tweet.content, tweet.postedAt.toString, tweet.createdAt.toString, tweet.updatedAt.toString)
    })

  // Slick3.3はMySQLの日付関連をほぼString(varchar)で処理しようとする
  val id:        Rep[Long]   = column[Long]  ("id",      O.AutoInc, O.PrimaryKey)
  val content:   Rep[String] = column[String]("content", O.Length(120,varying=true))
  val postedAt:  Rep[String] = column[String]("posted_at")
  val createdAt: Rep[String] = column[String]("created_at")
  val updatedAt: Rep[String] = column[String]("updated_at")
}

