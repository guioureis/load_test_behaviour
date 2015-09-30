package com.rest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.rest.data.CustomerTestData._
import scala.io.Source
import java.io._
import scala.collection.mutable.ArrayBuffer


class BehaviourSimulation extends io.gatling.core.scenario.Simulation {

  val httpProtocol = http.baseURL(baseURL)

  var hashes = ArrayBuffer[String]()
  var cpfs = ArrayBuffer[String]()

  val scn = scenario("Consult a unique cpf").repeat(repeatCount) {

    val writer = new PrintWriter(new File("result.csv"))

    writer.println("hash|response")

    val bufferedSource = Source.fromFile(hashInputFile)

    for (line <- bufferedSource.getLines) {
      if(line != "cpf,hash") {
        cpfs += line.split(",")(0)
        hashes += line.split(",")(1)
      }
    }

    bufferedSource.close
    
    foreach(hashes, "hash", "i") {
        exec(
          http(session => "Consult a cpf")
            .post(consumerBehaviourEndpoint)
            .body(serialize(getQuery(token, "${hash}")))
            .check(status is 200)
            .check(jsonPath("$.users").saveAs("resp")))
        .exec( 
          session => {
            writer.println(cpfs(session("i").as[Int]) + "|" + session("resp").as[String])
            writer.flush()
            session
          }
        )
    }
  }
  
  setUp(
    scn.inject(atOnceUsers(simultaneousUsers))
  ) 
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(percentSuccess))
}