import java.net._
import java.io._

import scala.actors._
import scala.actors.Actor._

case class Echo(socket: Socket)

object Service extends Actor {

 implicit def inputStreamWrapper(in: InputStream) =
  new BufferedReader(new InputStreamReader(in))

 implicit def outputStreamWrapper(out: OutputStream) =
  new PrintWriter(new OutputStreamWriter(out))

 def echo(in: BufferedReader, out: PrintWriter) {
   val line = in.readLine()
   out.println(line)
   out.flush()
 }

 def act() {
  loop {
   receive {
    case Echo(socket) =>
     actor {
      echo(socket.getInputStream(), socket.getOutputStream())
      socket.close
     }
   }
  }
 }

}

object EchoServer {
 Service.start

 val serverSocket = new ServerSocket(4444)
 
 def start() {
  while(true) {
   println("about to block")
      val clientSocket = serverSocket.accept()
   Service ! Echo(clientSocket)
   println("back from actor")
  }
 }
}

