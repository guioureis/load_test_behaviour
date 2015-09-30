package com.rest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.rest.data.CustomerTestData._
import scala.util.Random
import scala.io.Source
import java.io._
import scala.collection.mutable.ArrayBuffer


class BehaviourLoadSimulation extends io.gatling.core.scenario.Simulation {

  val httpProtocol = http.baseURL(baseURL)
  
  var hashes = prepareHashes 

  val scn = scenario("Consult cpfs by bulk").repeat(repeatCount) {

    exec(
      http(session => "Consult 2 clients")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, prepareArray(2))))
        .check(status is 200)
        .check(jsonPath("$.status").is("ok"))
        .check(jsonPath("$").saveAs("resp")))
    .exec(
      http(session => "Consult 10 clients")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, prepareArray(10))))
        .check(status is 200)
        .check(jsonPath("$.status").is("ok"))
        .check(jsonPath("$").saveAs("resp")))
    .exec(
      http(session => "Consult 100 clients")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, prepareArray(100))))
        .check(status is 200)
        .check(jsonPath("$.status").is("ok"))
        .check(jsonPath("$").saveAs("resp")))
    .exec(
      http(session => "Consult 4000 clients")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, prepareArray(4000))))
        .check(status is 200)
        .check(jsonPath("$.status").is("ok"))
        .check(jsonPath("$").saveAs("resp")))
    /*.exec( session => { 
      println(" ----->> " + session("resp").as[String])
      session
      })*/
  }
  
  def prepareHashes = {
    var hashes = ArrayBuffer[String]()
  
    val bufferedSource = Source.fromFile(hashInputFile)

    for (line <- bufferedSource.getLines) {
      if(line != "cpf,hash") {
        hashes += line.split(",")(1)
      }
    }

    bufferedSource.close

    hashes
  }

  def prepareArray(size:Int) = {
    var temp = ArrayBuffer[String]()
  
    for(i <- 0 to size-1) {
      temp += hashes(Random.nextInt(hashes.size-1))
    }

    temp
  }

  setUp(
    scn.inject(atOnceUsers(simultaneousUsers))
  ) 
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(percentSuccess))
}