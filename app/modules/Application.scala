package modules

import akka.actor.ActorSystem
import datasources.AlphaVantageActor
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Singleton

@Singleton
class Application @Inject() (system: ActorSystem) {

  val helloActor = system.actorOf(AlphaVantageActor.props)

}