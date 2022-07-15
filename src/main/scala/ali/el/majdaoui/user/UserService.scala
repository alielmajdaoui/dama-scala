package ali.el.majdaoui.user

import ali.el.majdaoui.effects.State
import ali.el.majdaoui.user.domain.{User, UserId, UserName}

trait UserService[F[_]] {
  def create(userId: UserId, userName: UserName): F[Either[String, Unit]]
  def get(userId: UserId): F[Option[User]]
}

object UserService {

  sealed trait UserError
  object UserError

  def apply[F[_]](state: State[F]): UserService[F] = new UserService[F] {
    override def create(userId: UserId, userName: UserName): F[Either[String, Unit]] = ???

    override def get(userId: UserId): F[Option[User]] = ???
  }
}
