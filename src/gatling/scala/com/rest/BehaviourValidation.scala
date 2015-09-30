package com.rest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.rest.data.CustomerTestData._
import java.io._
import scala.collection.mutable.ArrayBuffer


class BehaviourValidation extends io.gatling.core.scenario.Simulation {

  val httpProtocol = http.baseURL(baseURL)
  
  val clientHash = "2938d20ebeef45cda1d8da6420a2a2b9a1c9b2af"
  val clientHash2 = "a9a56393925fc84e3f6ffedd29773a8991aca424"
  val clientHash3 = "84e566e90ed00ae10efd91c3be40f5e07b40faca"

  val scn = scenario("Consult a client behaviour by cpf").repeat(repeatCount) {

    var hashes = ArrayBuffer[String]()

    hashes += clientHash
    hashes += clientHash2
    hashes += clientHash3

    var hashesWithInvalid = ArrayBuffer[String]()

    hashesWithInvalid += clientHash
    hashesWithInvalid += clientHash2
    hashesWithInvalid += "invalidHash"


    exec(
      http(session => "Consult a valid user")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, clientHash)))
        .check(status is 200)
        .check(jsonPath("$.users[0].acxiomHash").is(clientHash)))
    .exec( 
      http(session => "Consult a invalid user")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, "invalidHash")))
        .check(status is 200)
        .check(jsonPath("$.users[0]").notExists))
    .exec( 
      http(session => "Consult a user with invalid token")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery("invalidToken", clientHash)))
        .check(status is 200)
        .check(jsonPath("$.status").is("error"))
        .check(jsonPath("$.message").is("Authentication Error.")))
    .exec(
      http(session => "Consult multiple valid users")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, hashes)))
        .check(status is 200)
        .check(jsonPath("$.users[0].acxiomHash").is(clientHash))
        .check(jsonPath("$.users[1].acxiomHash").is(clientHash2))
        .check(jsonPath("$.users[2].acxiomHash").is(clientHash3)))
    .exec(
      http(session => "Consult multiple with invalid users")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, hashesWithInvalid)))
        .check(status is 200)
        .check(jsonPath("$.users[0].acxiomHash").is(clientHash))
        .check(jsonPath("$.users[1].acxiomHash").is(clientHash2))
        .check(jsonPath("$.users[2]").notExists))
    .exec(
      http(session => "Consult without hashes")
        .post(consumerBehaviourEndpoint)
        .body(serialize(getQuery(token, ArrayBuffer[String]())))
        .check(status is 200))
  }
    
  setUp(
    scn.inject(atOnceUsers(simultaneousUsers))
  ) 
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(percentSuccess))
}