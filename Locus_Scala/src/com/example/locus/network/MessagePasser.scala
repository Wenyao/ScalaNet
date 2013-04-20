import java.io._
import java.net._
import com.example.locus.core.ICore
import com.example.locus.core.CoreFacade
import com.example.locus.entity.User
import com.example.locus.entity.Message
import com.example.locus.util.SerializeHelper

object MessagePasser {
    //TODO add coreHandler
  	//val user = new User("Alice", Sex.Female, "192.168.1.1", 0, 0) 
  	var coreHandler: ICore = _
	
  	def listen(port: Integer, core: ICore): Unit = {
  		coreHandler = core
  	  
  		//the listening thread for handling user profile request 
  		actors.Actor.actor{
  			val ss = new ServerSocket(port+1)
  			while(true){
  				println("listening for profile request...")
				val sock = ss.accept()
				actors.Actor.actor{
  					val oos = new ObjectOutputStream(sock.getOutputStream())
  					
  					//TODO getCurrentUser from core
  					//oos.writeObject(user)
  					oos.writeObject(coreHandler.getCurrentUser());
  					
  					oos.flush()
				}
  			}
  			ss.close()
  		}
  		//the main thread for messaging
  		//TODO add actor for main thread
  		actors.Actor.actor{
  			val ss = new ServerSocket(port)
  			while(true){
  				println("listening for message...")
  				val sock = ss.accept()
  				actors.Actor.actor{
  					val is = new BufferedInputStream(sock.getInputStream())
  					val os = new PrintStream(new BufferedOutputStream(sock.getOutputStream()))
  					val oos = new ObjectOutputStream(sock.getOutputStream())
  					os.println("Connected to Server Wenyao Li, start chatting:")
  					os.flush()
  					actors.Actor.actor {
  						while(true){		
  							while(is.available() < 1) {Thread.sleep(100)}
  							val buf = new Array[Byte] (is.available)
  							is.read(buf)
  							val recvmsg = new String(buf)
  							println(recvmsg)
							//TODO call coreHandler's onReceiveMessage
  							val message : Message = SerializeHelper.deserialize(buf).asInstanceOf[Message]
  							coreHandler.onReceiveMessage(message.getSrc(), message.getData().asInstanceOf[String]);
  						}
  					}
  					while(true){
						val sendmsg = readLine
						os.println(sendmsg)
						os.flush()
					}
  				}
  			}
		}
		//ss.close()
    }
	
	def connect(ip: String, port: Integer): Unit = {
  		val sock = new Socket(ip, port)
  		val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
		val os = new PrintStream(sock.getOutputStream())
  		val ois = new ObjectInputStream(sock.getInputStream())
		val bois = new BufferedInputStream(ois)
  		var flag = true
		os.println("Connected with client ABC, start chatting:")
		actors.Actor.actor {
			while(flag){		
				if(is.ready){
					val output = is.readLine
					println(output)
				}
				if(bois.available() >= 1){
					val user = ois.readObject
					println(user.toString())
				}
				Thread.sleep(100)
			}
		} 
		while(flag){
			val sendmsg = readLine
			if (sendmsg == "quit") {
			  flag = false
			}else if (sendmsg == "check"){
				checkProfile(ip, port)
			}else {
			  os.println(sendmsg)
			  os.flush()
			}
		}
		sock.close()
    }

	def checkProfile(ip: String, port: Integer): Object = {
  		val sock = new Socket(ip, port+1)
		val ois = new ObjectInputStream(sock.getInputStream())
		val user = ois.readObject
  		println(user.toString())
		sock.close()
		return user
    }
	
	def main(args: Array[String]): Unit = {
		listen(2222, CoreFacade.getInstance()) //for server testing
		//connect("localhost", 2222) for client testing
	}
}