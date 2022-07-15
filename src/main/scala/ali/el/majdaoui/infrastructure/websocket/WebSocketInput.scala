package ali.el.majdaoui.infrastructure.websocket

sealed trait WebSocketInput {
  val from: String
}

object WebSocketInput {

  case class Help(from: String) extends WebSocketInput
  case class StartNewGame(from: String, withUser: String) extends WebSocketInput
  case class InvalidCommand(from: String, message: String) extends WebSocketInput
  case class Disconnect(from: String) extends WebSocketInput

  private def getWords(str: String): List[String] = str.trim.split(" ").map(_.trim).toList

  def parse(from: String, message: String): WebSocketInput = getWords(message) match {
    case "help" :: Nil                => Help(from)
    case "start" :: withUserId :: Nil => StartNewGame(from, withUserId)
    case "start" :: _                 => InvalidCommand(from, "`start` command takes only one argument")
    case Nil                          => InvalidCommand(from, "Empty command")
    case _                            => InvalidCommand(from, "Unknown command")
  }
}
