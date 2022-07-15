package ali.el.majdaoui.user.domain

import java.util.UUID

final case class User(userId: UserId, userName: UserName)

final case class UserId(value: UUID) extends AnyVal
final case class UserName(value: String) extends AnyVal
