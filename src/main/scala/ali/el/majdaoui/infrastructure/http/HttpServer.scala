package ali.el.majdaoui.infrastructure.http

import cats.effect.{ConcurrentEffect, Sync, Timer}
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

trait HttpServer[F[_]]

object HttpServer {

  def apply[F[_]: ConcurrentEffect: Timer](
    executionContext: ExecutionContext,
    httpApp: HttpApp[F]
  ): F[BlazeServerBuilder[F]] =
    Sync[F].delay(
      BlazeServerBuilder[F](executionContext)
        .bindHttp(9393, "0.0.0.0")
        .withHttpApp(httpApp)
    )
}
