package ali.el.majdaoui

import ali.el.majdaoui.infrastructure.websocket.WebSocketOutput
import ali.el.majdaoui.infrastructure.{HttpAppApi, HttpServer, SharedState}
import cats.effect.{ExitCode, IO, IOApp}
import fs2.concurrent.Topic
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def run(args: List[String]): IO[ExitCode] = for {
    state      <- SharedState.make[IO]
    topic      <- Topic[IO, WebSocketOutput](WebSocketOutput.TopicInitial())
    httpAppApi <- HttpAppApi.make[IO](state, topic)
    httpServer <- HttpServer[IO](executionContext, httpAppApi.httpApp)
    _          <- httpServer.serve.compile.drain
  } yield ExitCode.Success
}
