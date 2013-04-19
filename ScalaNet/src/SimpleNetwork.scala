import java.net.ServerSocket
import java.io.BufferedInputStream
import java.io.PrintStream
import java.io.BufferedOutputStream
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader

object SimpleNetwork {

	def listen(port: Integer): Unit = {
		val ss = new ServerSocket(port)
		while(true){
			println("listening...")
			val sock = ss.accept()
			actors.Actor.actor{
				val is = new BufferedInputStream(sock.getInputStream())
				val os = new PrintStream(new BufferedOutputStream(sock.getOutputStream()))
				os.println("Connected to Server Wenyao Li, start chatting:")
				os.flush()
				actors.Actor.actor {
					while(true){		
						while(is.available() < 1) {Thread.sleep(100)}
						val buf = new Array[Byte] (is.available)
						is.read(buf)
						val recvmsg = new String(buf)
						println(recvmsg)
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
	
	def connect(ip: String, port: Integer): Unit = {
		val sock = new Socket(ip, port)
		val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
		val os = new PrintStream(sock.getOutputStream())
		var flag = true
		os.println("Connected with client ABC, start chatting:");
		actors.Actor.actor {
			while(flag){		
				if(is.ready){
					val output = is.readLine
					println(output)
				}
				Thread.sleep(100)
			}
		}
		while(flag){
			val sendmsg = readLine
			if (sendmsg == "quit") {
			  flag = false
			}
			else{
			  os.println(sendmsg)
			  os.flush()
			}
		}
		sock.close()
    }
	
	def main(args: Array[String]): Unit = {
		listen(2222)
	}
}