package ali.el.majdaoui.infrastructure

import cats.effect.Sync
import cats.{Defer, Monad}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final class WebRoutes[F[_]: Defer: Monad: Sync](state: State[F]) extends Http4sDsl[F] {

  private[infrastructure] val prefixPath = "/"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "test" =>
        Ok("cool")

      case GET -> Root / "state" =>
        for {
          users    <- state.getUsers
          response <- Ok(users.toString)
        } yield response
    }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
