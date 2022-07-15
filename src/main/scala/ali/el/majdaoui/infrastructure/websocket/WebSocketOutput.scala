package ali.el.majdaoui.infrastructure.websocket

sealed trait WebSocketOutput {
  def forUser(targetUser: String): Boolean
  def toString: String
}

object WebSocketOutput {

  case class ToUser(user: String, text: String) extends WebSocketOutput {
    def forUser(targetUser: String): Boolean = user == targetUser
    override def toString: String = text
  }

  case class ToMultipleUsers(users: Vector[String], text: String) extends WebSocketOutput {
    def forUser(targetUser: String): Boolean = users.contains(targetUser)
    override def toString: String = text
  }

  case class BroadcastMessage(text: String) extends WebSocketOutput {
    def forUser(targetUser: String): Boolean = true
    override def toString: String = text
  }

  case class TopicInitial() extends WebSocketOutput {
    def forUser(targetUser: String): Boolean = false
    override def toString: String = "n/a"
  }
}
