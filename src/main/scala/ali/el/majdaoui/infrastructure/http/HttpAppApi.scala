package ali.el.majdaoui.infrastructure.http

import ali.el.majdaoui.api.ws.{WebSocketClient, WebSocketOutput, WebSocketRoutes}
import ali.el.majdaoui.effects.State
import cats.effect.{ConcurrentEffect, Sync, Timer}
import cats.implicits._
import fs2.concurrent.Topic
import io.chrisdavenport.log4cats.Logger
import org.http4s.HttpApp
import org.http4s.implicits._

object HttpAppApi {

  def make[F[_]: Timer: ConcurrentEffect: Logger](
    state: State[F],
    wsClient: WebSocketClient[F],
    topic: Topic[F, WebSocketOutput]
  ): F[HttpAppApi[F]] = {
    Sync[F].delay(new HttpAppApi[F](state, wsClient, topic))
  }
}

final class HttpAppApi[F[_]: Timer: ConcurrentEffect: Logger](
  state: State[F],
  wsClient: WebSocketClient[F],
  topic: Topic[F, WebSocketOutput]
) {
  private val webRoutes = new WebRoutes[F](state).routes
  private val webSocketRoutes = new WebSocketRoutes[F](state, wsClient, topic).routes
  private val allRoutes = webRoutes <+> webSocketRoutes

  val httpApp: HttpApp[F] =
    allRoutes.orNotFound
}
