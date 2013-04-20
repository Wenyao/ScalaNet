package com.example.locus.network

import java.net._
import java.io._
import com.example.locus.entity.User
import com.example.locus.core.ICore

object CheckProfile {
  var coreHandler: ICore = _

  def listen(port: Integer, core: ICore): Unit = {
    val ss = new ServerSocket(port)
    coreHandler = core
    actors.Actor.actor {
      while (true) {
        println("listening...")
        val sock = ss.accept()
        actors.Actor.actor {
          val oos = new ObjectOutputStream(sock.getOutputStream())
          oos.writeObject(core.getCurrentUser())
          oos.flush()
        }
      }
    }
    ss.close()
  }

  def connect(ip: String, port: Integer): Object = {
    val sock = new Socket(ip, port)
    val ois = new ObjectInputStream(sock.getInputStream())
    val user = ois.readObject.asInstanceOf[User]
    println(user.toString())
    sock.close()
    //coreHandler.onReceiveUserProfile(user)
    return user
  }

  def main(args: Array[String]): Unit = {
    //listen(2222)
    //connect("localhost", 2222)
  }
}