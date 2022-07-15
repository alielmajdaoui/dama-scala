package ali.el.majdaoui.api.ws

import ali.el.majdaoui.effects.UUIDSupport
import cats.Monad
import cats.implicits._
import cats.effect.Sync
import cats.effect.concurrent.Ref
import io.chrisdavenport.log4cats.Logger
import org.http4s.Response
import org.http4s.server.websocket.WebSocketBuilder
import fs2.{Pipe, Stream}
import fs2.concurrent.Topic
import org.http4s.websocket.WebSocketFrame

import java.util.UUID

case class Client(id: ClientId)
case class ClientId(value: UUID) extends AnyVal

trait WebSocketClient[F[_]] {
  def create: F[Client]
  def delete(clientId: ClientId): F[Unit]
}

object WebSocketClient {

  def of[F[_]: Sync]: F[WebSocketClient[F]] =
    Ref.of(Map.empty[ClientId, Client]).map(apply(_))

  def apply[F[_]: Sync](state: Ref[F, Map[ClientId, Client]]): WebSocketClient[F] =
    new WebSocketClient[F] {

      def create: F[Client] = for {
        uuid <- UUIDSupport().random
        clientId = ClientId(uuid)
        client = Client(clientId)
        element = clientId -> client
        result <- state.modify[Client](current => (current + element, client))
      } yield result

      def delete(clientId: ClientId): F[Unit] = Sync[F].unit
    }

  def handleRequest[F[_]: Monad: Sync: Logger](
    topic: Topic[F, WebSocketOutput],
    state: WebSocketClient[F]
  ): F[Response[F]] =
    for {
      client <- state.create
      _      <- Logger[F].info(s"User `${client.id}` joined!")
      response <- WebSocketBuilder[F].build(
        toClient[F](client, topic, state),
        fromClient(client, topic, state)
      )
    } yield response

  private def toClient[F[_]: Sync: Logger](
    client: Client,
    topic: Topic[F, WebSocketOutput],
    state: WebSocketClient[F]
  ): Stream[F, WebSocketFrame.Text] = topic
    .subscribe(10)
    .evalMap { original =>
      Logger[F].info(original.getClass.toString) *>
      Sync[F].delay(original)
    }
    // Is there a way to send a message to a specific client without
    // filtering over the list of clients?
    // Or is this normal?
    .filter(_.forClient(client.id))
    .map(output => WebSocketFrame.Text(output.toString))

  private def fromClient[F[_]: Sync: Logger](
    client: Client,
    topic: Topic[F, WebSocketOutput],
    state: WebSocketClient[F]
  ): Pipe[F, WebSocketFrame, Unit] =
    _.collect {
      case WebSocketFrame.Text(text, _) =>
        WebSocketInput.parse(client, text)
      case WebSocketFrame.Close(_) =>
        WebSocketInput.Disconnect(client)
    }
      .evalMap(handleInput(_, state))
      .through(topic.publish)

  private def handleInput[F[_]: Sync: Logger](
    input: WebSocketInput,
    state: WebSocketClient[F]
  ): F[WebSocketOutput] =
    input match {
      case WebSocketInput.Help(from) =>
        Sync[F].delay[WebSocketOutput](
          WebSocketOutput.ToClient(from.id, "help will be available soon")
        )
      case WebSocketInput.StartNewGame(from, withUser) =>
        Sync[F].delay[WebSocketOutput](
          // WebSocketOutput.ToClient(withUser, s"`${from}` wants to play with `$withUser`")
          WebSocketOutput.BroadcastMessage(s"${from.id.toString} wants to play with `$withUser`")
        )
      case WebSocketInput.InvalidCommand(from, message) =>
        Sync[F].delay[WebSocketOutput](WebSocketOutput.ToClient(from.id, message))
      case WebSocketInput.Disconnect(from) =>
        Logger[F].info(s"`$from` disconnected") *>
          state.delete(from.id) *>
          Sync[F].delay[WebSocketOutput](
            WebSocketOutput.BroadcastMessage(s"`$from` disconnected")
          )
    }
}
