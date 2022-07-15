package ali.el.majdaoui.api.ws

import ali.el.majdaoui.effects.State
import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import cats.{Defer, Monad}
import fs2.concurrent.Topic
import fs2.{Pipe, Stream}
import io.chrisdavenport.log4cats.Logger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame

case class User(id: Int)

class WebSocketRoutes[F[_]: Defer: Monad: Sync: ConcurrentEffect: Logger](
  state: State[F],
  wsClient: WebSocketClient[F],
  topic: Topic[F, WebSocketOutput]
) extends Http4sDsl[F] {

  private[ws] val prefixPath = "/"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "ws" =>
      WebSocketClient.handleRequest[F](topic, wsClient)
    }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
