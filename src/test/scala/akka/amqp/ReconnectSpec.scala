package akka.amqp

import akka.testkit.AkkaSpec

class ReconnectSpec extends AkkaSpec {

  "ReonnectionTimeoutGenerator" should {

    val timeoutGenerator = new ReconnectTimeoutGenerator

    "start with 1" in {
      timeoutGenerator.nextTimeoutSec(10) shouldBe 1
    }
    "continue to 2" in {
      timeoutGenerator.nextTimeoutSec(10) shouldBe 2
    }
    "continue to 3" in {
      timeoutGenerator.nextTimeoutSec(10) shouldBe 3
    }
    "continue to 5" in {
      timeoutGenerator.nextTimeoutSec(10) shouldBe 5
    }
    "continue to 8" in {
      timeoutGenerator.nextTimeoutSec(10) shouldBe 8
    }
    "max out at 10" in {
      timeoutGenerator.nextTimeoutSec(10) shouldBe 10
    }
    "go back to 1 after reset" in {
      timeoutGenerator.reset()
      timeoutGenerator.nextTimeoutSec(10) shouldBe 1
    }
  }
}
