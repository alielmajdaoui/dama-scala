package ali.el.majdaoui

import ali.el.majdaoui.infrastructure.{HttpAppApi, HttpServer}
import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def run(args: List[String]): IO[ExitCode] = for {
    httpAppApi <- HttpAppApi.make[IO]()
    httpServer <- HttpServer[IO](executionContext, httpAppApi.httpApp)
    _          <- httpServer.serve.compile.drain
  } yield ExitCode.Success
}
