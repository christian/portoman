package modules

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import datasources._
import play.api.inject.{SimpleModule, _}
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure() = {

//    bind(classOf[AlphaVantageScheduler]).asEagerSingleton()
//    bindActor[AlphaVantageActor]("alpha-vantage-fetcher")

    bind(classOf[IEXScheduler]).asEagerSingleton()
    bindActor[IEXActor]("iex-fetcher")

  }
}