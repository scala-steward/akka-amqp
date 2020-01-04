package akka

package object amqp {

  object RabbitQueue extends com.rabbitmq.client.AMQP.Queue {
    type BindOk    = com.rabbitmq.client.AMQP.Queue.BindOk
    type DeclareOk = com.rabbitmq.client.AMQP.Queue.DeclareOk
    type DeleteOk  = com.rabbitmq.client.AMQP.Queue.DeleteOk
    type PurgeOk   = com.rabbitmq.client.AMQP.Queue.PurgeOk
  }

  object RabbitExchange extends com.rabbitmq.client.AMQP.Exchange {
    type BindOk    = com.rabbitmq.client.AMQP.Exchange.BindOk
    type DeclareOk = com.rabbitmq.client.AMQP.Exchange.DeclareOk
    type UnbindOk  = com.rabbitmq.client.AMQP.Exchange.UnbindOk
  }

  type ReturnListener          = com.rabbitmq.client.ReturnListener
  type ConfirmListener         = com.rabbitmq.client.ConfirmListener
  type BasicProperties         = com.rabbitmq.client.AMQP.BasicProperties
  type Envelope                = com.rabbitmq.client.Envelope
  type DefaultConsumer         = com.rabbitmq.client.DefaultConsumer
  type ShutdownListener        = com.rabbitmq.client.ShutdownListener
  type ShutdownSignalException = com.rabbitmq.client.ShutdownSignalException
  type AlreadyClosedException  = com.rabbitmq.client.AlreadyClosedException
  type ConnectionFactory       = com.rabbitmq.client.ConnectionFactory
  type RabbitConnection        = com.rabbitmq.client.Connection
  type RabbitChannel           = com.rabbitmq.client.Channel
}
