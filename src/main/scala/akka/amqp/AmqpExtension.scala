package akka.amqp
import akka.pattern.ask
import scala.language.implicitConversions
import akka.actor._
import com.typesafe.config.Config
import akka.actor.ExtensionIdProvider
import akka.actor.ExtensionId
import akka.actor.ExtendedActorSystem
import akka.actor.ActorSystem
import scala.concurrent.Future
import scala.concurrent.Await
import reflect.ClassTag
import akka.pattern.ask
import scala.concurrent.duration._
object AmqpExtension extends ExtensionId[AmqpExtensionImpl] with ExtensionIdProvider {

  override def lookup()                                                        = this
  override def createExtension(system: ExtendedActorSystem): AmqpExtensionImpl = new AmqpExtensionImpl()(system)
}

class AmqpExtensionImpl(implicit val _system: ActorSystem) extends Extension {
  implicit val settings  = new AmqpSettings(_system.settings.config.getConfig("akka.amqp.default"))
  implicit val extension = this

  val connectionActor = _system.actorOf(Props(new ConnectionActor(new ConnectionFactory, settings)), "amqp-connection")

  def createChannel = {
    implicit val to = akka.util.Timeout(5.seconds)
    (connectionActor ? CreateChannel()).mapTo[ActorRef]
  }

  //private implicit val timeout = akka.util.Timeout(settings.interactionTimeout)

  def withTempChannel[T: ClassTag](callback: RabbitChannel => T): Future[T] = {
    ???
    //    withConnection { conn =>
    //      val ch = conn.createChannel()
    //      try {
    //        callback(ch)
    //      } finally {
    //        if (ch.isOpen) { ch.close() }
    //      }
    //    }
  }

}

class AmqpSettings(config: Config) {
  import scala.concurrent.duration
  import scala.jdk.CollectionConverters._
  //durationIn
  val addresses: Seq[String]        = config.getStringList("addresses").asScala.toSeq
  val user: String                  = config.getString("user")
  val pass: String                  = config.getString("pass")
  val vhost: String                 = config.getString("vhost")
  val amqpHeartbeat: FiniteDuration = DurationLong(config.getDuration("heartbeat", MILLISECONDS)).milli
  val maxReconnectDelay: Duration   = DurationLong(config.getDuration("max-reconnect-delay", MILLISECONDS)).milli
  val channelThreads: Int           = config.getInt("channel-threads")
  val interactionTimeout: Duration  = DurationLong(config.getDuration("interaction-timeout", MILLISECONDS)).milli
  val channelCreationTimeout
      : Duration = DurationLong(config.getDuration("channel-creation-timeout", MILLISECONDS)).milli
  val channelReconnectTimeout
      : Duration = DurationLong(config.getDuration("channel-reconnect-timeout", MILLISECONDS)).milli
  val publisherConfirmTimeout: FiniteDuration = DurationLong(
    config.getDuration("publisher-confirm-timeout", MILLISECONDS)
  ).milli
}
