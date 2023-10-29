package models

import collection.mutable
import scala.annotation.meta.getter

object MessageBoardModel{
    private val users = mutable.Map[String, String]("mlewis" -> "prof", "web" -> "apps")
    private val messages = mutable.Map[String, List[String]]("mlewis" -> List("teach", "shoot hoops", "be amazing"), "web" -> List("Do the tasks", "Read FRS"))
    private val generalMessages = mutable.ListBuffer("This is a general message.")

    def validateUser(username: String, password: String): Boolean = {
        users.get(username).map(_ == password).getOrElse(false)
    }

    def createUser(username: String, password: String): Boolean = {
        if (users.contains(username)) false else{
            users(username) = password
            true
        }
    }

    def getMessages(username: String): Seq[String] = { //get personal messages 
        messages.get(username).getOrElse(Nil)
    }

    def getGenMessages(): Seq[String] = {
        generalMessages.toList
    }

    def getUsers(): mutable.Map[String, String] = users


    def addMessage(sender: String, recipient: Option[String], message: String): Unit = {
        recipient match {
            case Some(recipientUsername) =>
            // Personal message
            val recipientMessages = messages.getOrElse(recipientUsername, Nil)
            val updatedRecipientMessages = recipientMessages :+ message
            messages.update(recipientUsername, updatedRecipientMessages)
            case None =>
            // General message
            generalMessages += message
        }
    }



    def removeMessage(username: String, index: Int): Boolean = ???

}