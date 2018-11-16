package modules

import akka.actor.ActorSystem
import datasources.{AlphaVantageActor, IEXActor}
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import javax.inject.Singleton

@Singleton
class Application @Inject() (system: ActorSystem) {

  //val alphaVantageActor = system.actorOf(AlphaVantageActor.props)
  val iexActor = system.actorOf(IEXActor.props)

}