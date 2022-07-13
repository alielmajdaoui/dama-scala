package ali.el.majdaoui.infrastructure

import cats.{Defer, Monad}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final class WebRoutes[F[_]: Defer: Monad]() extends Http4sDsl[F] {

  private val prefixPath = "/"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "test" =>
      Ok()
    }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
