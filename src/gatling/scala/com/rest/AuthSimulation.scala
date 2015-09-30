package com.rest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.rest.data.CustomerTestData._


class AuthSimulation extends io.gatling.core.scenario.Simulation {

  val httpProtocol = http.baseURL(baseURL)

  val scn1 = scenario("Authenticate a user").repeat(repeatCount) {
    exec(
      http(session => "Valid user")
        .post(authEndpoint)
        .body(serialize(getUser))
        .check(status is 200)
        .check(jsonPath("$.token"))
    )
    .exec(
      http(session => "Invalid user")
        .post(authEndpoint)
        .body(serialize(getInvalidUser))
        .check(status is 200)
        .check(jsonPath("$.status").is("error"))
    )
    .exec(
      http(session => "Wrong password")
        .post(authEndpoint)
        .body(serialize(getInvalidPassword))
        .check(status is 200)
        .check(jsonPath("$.status").is("error"))
        .check(jsonPath("$.message").is("Invalid credentials."))
    )
    .exec(
      http(session => "Missing parameters")
        .post(authEndpoint)
        .body(serialize(new Object()))
        .check(status is 200)
        .check(jsonPath("$.status").is("error"))
        .check(jsonPath("$.message").is("Invalid credentials."))
    )
  }
  
  setUp(
    scn1.inject(atOnceUsers(simultaneousUsers))
  ) 
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(percentSuccess))
}