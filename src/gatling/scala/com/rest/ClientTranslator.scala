package com.rest

import io.gatling.core.Predef._
import scala.io.Source
import java.io._


class ClientTranslator extends io.gatling.core.scenario.Simulation with SimulationConfig {

  val scn = scenario("Read clients cpf and apply the hash algorith").repeat(1) {

    exec( session => {
      val bufferedSource = Source.fromFile(cpfInputFile)
      val writer = new PrintWriter(new File(hashOutputFile))

      writer.println("cpf,hash") // text for columns

      for (line <- bufferedSource.getLines) {
        val cols = line.split(",").map(_.trim)
        val cpf = cols(0)

        try {
          def temp = cpf.replaceAll("[-]", "").toLong + provider + cpf.takeRight(1)

          val md = java.security.MessageDigest.getInstance("SHA-1")
          
          def hash = md.digest(temp.getBytes("UTF-8")).map("%02x".format(_)).mkString

          println(cpf + " -> " + temp + " -> " + hash)

          writer.println(cpf + "," + hash)
        } catch {
          case e: Exception => println("exception caught: " + e);
        }
      }

      bufferedSource.close
      writer.close()
    
      session  
    })
  }

  setUp(scn.inject(atOnceUsers(1)))
}