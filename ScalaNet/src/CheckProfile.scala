import java.net._
import java.io._

object CheckProfile {
  	val user = new User("Alice", Sex.Female, "192.168.1.1", 0, 0) 
	
  	def listen(port: Integer): Unit = {
		val ss = new ServerSocket(port)
		while(true){
		    println("listening...")
			val sock = ss.accept()
			actors.Actor.actor{
				val oos = new ObjectOutputStream(sock.getOutputStream())
				oos.writeObject(user)
				oos.flush()
			}
		}
		ss.close()
    }
	
	def connect(ip: String, port: Integer): Object = {
  		val sock = new Socket(ip, port)
		val ois = new ObjectInputStream(sock.getInputStream())
		val user = ois.readObject
  		println(user.toString())
		sock.close()
		return user
    }
	
	def main(args: Array[String]): Unit = {
		listen(2222)
	}
}