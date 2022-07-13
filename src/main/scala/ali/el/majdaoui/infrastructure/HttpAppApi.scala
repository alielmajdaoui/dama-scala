package ali.el.majdaoui.infrastructure

import cats.implicits._
import cats.effect.{ConcurrentEffect, Sync, Timer}
import org.http4s.HttpApp
import org.http4s.implicits._

object HttpAppApi {
  def make[F[_]: Timer: ConcurrentEffect](): F[HttpAppApi[F]] = Sync[F].delay(new HttpAppApi[F])
}

final class HttpAppApi[F[_]: Timer: ConcurrentEffect] {
  private val webRoutes = new WebRoutes[F].routes
  private val webSocketRoutes = new WebSocketRoutes[F].routes
  private val allRoutes = webRoutes <+> webSocketRoutes

  val httpApp: HttpApp[F] =
    allRoutes.orNotFound
}
