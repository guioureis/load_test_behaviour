package com.rest

import akka.event.slf4j.SLF4JLogging
import com.typesafe.config.{ConfigException, ConfigFactory}
import scala.util.Try

trait SimulationConfig extends SLF4JLogging {

  private[this] val config = ConfigFactory.load()

  val baseURL = getRequiredString("service.host")
  val repeatCount = getRequiredInt("service.repeat")
  val simultaneousUsers = getRequiredInt("service.simultaneous_users")
  // Percent of successful service responses when the simulation is considered to be successful.
  val percentSuccess = Try(config.getInt("service.percent_success")).getOrElse(100)


  val authEndpoint = getRequiredString("auth.endpoint")
  val username = getRequiredString("auth.username")
  val password = getRequiredString("auth.password")  


  val consumerBehaviourEndpoint = getRequiredString("behaviour.endpoint")
  val hashInputFile = getRequiredString("behaviour.hash_input_file")
  val token = getRequiredString("behaviour.token")


  val cpfInputFile = getRequiredString("translator.cpf_input_file")
  val hashOutputFile = getRequiredString("translator.hash_output_file")
  val provider = getRequiredString("translator.provider")

  
  def getRequiredString(path: String) = {
    Try(config.getString(path)).getOrElse {
      handleError(path)
    }
  }

  def getRequiredInt(path: String) = {
    Try(config.getInt(path)).getOrElse {
      handleError(path)
    }
  }

  def getRequiredStringList(path: String) = {
    Try(config.getStringList(path)).getOrElse {
      handleError(path)
    }
  }

  private[this] def handleError(path: String) = {
    val errMsg = s"Missing required configuration entry: $path"
    log.error(errMsg)
    throw new ConfigException.Missing(errMsg)
  }

}
