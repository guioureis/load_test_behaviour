package com.rest.data

import java.text.SimpleDateFormat
import com.rest.SimulationConfig
import net.liftweb.json.Serialization
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object CustomerTestData extends SimulationConfig {

  implicit val formats = net.liftweb.json.DefaultFormats

  def serialize(body:Object) = {
    StringBody(Serialization.write(body))
  }

  def getUser: Customer = {
    Customer("",
      username,
      password,
      Array("")
    )
  }

  def getInvalidUser: Customer = {
    Customer("", "invalid", "", Array(""))
  }

  def getInvalidPassword: Customer = {
    Customer("", username, "wrong", Array(""))
  }

  def getQuery(token:String, hash:String): Customer = {
    getQuery(
      token,
      Array(hash)
    )
  }

  def getQuery(token:String, hashes:Seq[String]): Customer = {
    Customer(token,
      username,
      "",
      hashes
    )
  }
}
