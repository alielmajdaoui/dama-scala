package ali.el.majdaoui.api.ws

sealed trait WebSocketInput {
  val fromClient: Client
}

object WebSocketInput {

  case class Help(fromClient: Client) extends WebSocketInput
  case class StartNewGame(fromClient: Client, withClient: String) extends WebSocketInput
  case class InvalidCommand(fromClient: Client, message: String) extends WebSocketInput
  case class Disconnect(fromClient: Client) extends WebSocketInput

  private def getWords(str: String): List[String] = str.trim.split(" ").map(_.trim).toList

  def parse(fromClient: Client, message: String): WebSocketInput = getWords(message) match {
    case "help" :: Nil                => Help(fromClient)
    case "start" :: withClient :: Nil => StartNewGame(fromClient, withClient)
    case "start" :: _ =>
      InvalidCommand(fromClient, "`start` command takes only one argument")
    case Nil => InvalidCommand(fromClient, "Empty command")
    case _   => InvalidCommand(fromClient, "Unknown command")
  }
}
