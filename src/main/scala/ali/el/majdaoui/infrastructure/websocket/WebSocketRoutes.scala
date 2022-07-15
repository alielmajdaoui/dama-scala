package ali.el.majdaoui.infrastructure.websocket

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
  topic: Topic[F, WebSocketOutput]
) extends Http4sDsl[F] {

  private[infrastructure] val prefixPath = "/"

  private def toClient(userId: String): Stream[F, WebSocketFrame.Text] = topic
    .subscribe(10)
    .evalMap { original =>
      Logger[F].info(original.getClass.toString) *>
      Sync[F].delay(original)
    }
    // Is there a way to send a message to a specific client without
    // filtering over list of users?
    // Or is this normal?
    .filter(_.forUser(userId))
    .map(output => WebSocketFrame.Text(output.toString))

  private def fromClient(userId: String): Pipe[F, WebSocketFrame, Unit] =
    _.collect {
      case WebSocketFrame.Text(text, _) =>
        WebSocketInput.parse(userId, text)
      case WebSocketFrame.Close(_) =>
        WebSocketInput.Disconnect(userId)
    }
      .evalMap {
        case WebSocketInput.Help(from) =>
          Sync[F].delay[WebSocketOutput](
            WebSocketOutput.ToUser(from, "help will be available soon")
          )
        case WebSocketInput.StartNewGame(from, withUser) =>
          Sync[F].delay[WebSocketOutput](
            WebSocketOutput.ToUser(withUser, s"`$from` wants to play with `$withUser`")
          )
        case WebSocketInput.InvalidCommand(from, message) =>
          Sync[F].delay[WebSocketOutput](WebSocketOutput.ToUser(from, message))
        case WebSocketInput.Disconnect(from) =>
          Logger[F].info(s"`$from` disconnected") *>
            state.deleteUser(from) *>
            Sync[F].delay[WebSocketOutput](
              WebSocketOutput.BroadcastMessage(s"`$from` disconnected")
            )
      }
      .through(topic.publish)

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "ws" =>
      for {
        userId <- state.createUser
        _      <- Logger[F].info(s"User `$userId` joined!")
        response <- WebSocketBuilder[F].build(
          toClient(userId),
          fromClient(userId)
        )
      } yield response
    }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
