package com.example.locus.network

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import com.example.locus.core.ICore
import com.example.locus.entity.Message
import com.example.locus.entity.User
import java.io.Serializable

object MessagePasser {
    //TODO add coreHandler
  	//val user = new User("Alice", Sex.Female, "192.168.1.1", 0, 0)
  	var coreHandler: ICore = _
  	var stop : Boolean = false;
	
  	def listen(port: Integer, core: ICore): Unit = {
  		coreHandler = core
  		stop = false;
  		//the listening thread for handling user profile request 
  		actors.Actor.actor{
  			val ss = new ServerSocket(port+1)
  			while(!stop){
  				println("listening for profile request...")
				val sock = ss.accept()
				actors.Actor.actor{
  					val oos = new ObjectOutputStream(sock.getOutputStream())					
  					//TODO getCurrentUser from core
  					//oos.writeObject("USER!")
  					oos.writeObject(coreHandler.getCurrentUser()) 	
  					oos.flush()
				}
  			}
  			ss.close()
  		}
  		//the main thread for messaging
  		//TODO add actor for main thread
  		actors.Actor.actor{
  			val ss = new ServerSocket(port)
  			while(!stop){
  				//println("listening for message...")
  				val sock = ss.accept()
  				actors.Actor.actor{
  					//val is = new BufferedInputStream(sock.getInputStream())
  					//val os = new PrintStream(new BufferedOutputStream(sock.getOutputStream()))
  					//val oos = new ObjectOutputStream(sock.getOutputStream())
  					val ois = new ObjectInputStream(sock.getInputStream())
  					val recvmsg = ois.readObject.asInstanceOf[Message]
  					Message.refreshId(recvmsg.getId())
					//TODO call coreHandler's onReceiveMessage
 					//val message : Message = SerializeHelper.deserialize(buf).asInstanceOf[Message]
  					//println(recvmsg.toString())
  					coreHandler.onReceiveMessage(recvmsg) 					
  				}
  			}
		}
		//ss.close()
    }
	
	def sendMessage(src: User, dest: User, port: Integer, kind : String, content: Serializable): Unit = {
  		val sock = new Socket(dest.getIp(), port)
  		//val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
		//val os = new PrintStream(sock.getOutputStream())
  		//val ois = new ObjectInputStream(sock.getInputStream())
  		val oos = new ObjectOutputStream(sock.getOutputStream())
		//val bois = new BufferedInputStream(ois)
		val sendmsg = new Message(src, dest, kind, content)
  		sendmsg.setId()
  		oos.writeObject(sendmsg) 
  		oos.flush()
		sock.close()
    }
	
	def stopListen() : Unit = {
	  stop = true;
	}

	def checkProfile(target: User, port: Integer): Object = {
  		val sock = new Socket(target.getIp(), port+1)
		val ois = new ObjectInputStream(sock.getInputStream())
		val user = ois.readObject
  		println(user.toString())
		sock.close()
		return user
    }
	
	def main(args: Array[String]): Unit = {
		listen(2222, null) //for server testing
		//connect("localhost", 2222) for client testing
	}
}