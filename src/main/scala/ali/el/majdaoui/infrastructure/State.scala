package ali.el.majdaoui.infrastructure

import cats.implicits._
import cats.effect.Sync
import cats.effect.concurrent.Ref

trait State[F[_]] {
  def addUser(id: String): F[String]
  def createUser: F[String]
  def deleteUser(userId: String): F[Unit]
  def getUsers: F[Map[String, String]]
}

object SharedState {

  def make[F[_]: Sync]: F[State[F]] =
    Ref.of[F, Map[String, String]](Map.empty[String, String]).map { ref =>
      new State[F] {
        def addUser(id: String): F[String] =
          UUIDSupport().random flatMap { uuid =>
            ref.modify(current => (current + (uuid.toString -> id), uuid.toString))
          }

        def createUser: F[String] = for {
          uuid <- UUIDSupport().random
          uuidString = uuid.toString
          userId <- ref.modify(current => (current + (uuidString -> "nothing yet"), uuidString))
        } yield userId

        def deleteUser(userId: String): F[Unit] = ref.update(current => {
          if (current.contains(userId)) {
            current - userId
          } else {
            current
          }
        })

        def getUsers: F[Map[String, String]] = ref.get
      }
    }
}
