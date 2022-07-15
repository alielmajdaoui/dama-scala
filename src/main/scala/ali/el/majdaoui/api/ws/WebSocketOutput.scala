package ali.el.majdaoui.api.ws

sealed trait WebSocketOutput {
  def forClient(target: ClientId): Boolean
  def toString: String
}

object WebSocketOutput {

  case class ToClient(fromClient: ClientId, text: String) extends WebSocketOutput {
    def forClient(target: ClientId): Boolean = fromClient == target
    override def toString: String = text
  }

  case class ToMultipleClients(clients: Vector[ClientId], text: String) extends WebSocketOutput {
    def forClient(target: ClientId): Boolean = clients.contains(target)
    override def toString: String = text
  }

  case class BroadcastMessage(text: String) extends WebSocketOutput {
    def forClient(target: ClientId): Boolean = true
    override def toString: String = text
  }

  case class TopicInitial() extends WebSocketOutput {
    def forClient(target: ClientId): Boolean = false
    override def toString: String = "n/a"
  }
}
