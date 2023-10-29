package controllers

import javax.inject._
import play.api.mvc._
import play.api.i18n._
import java.lang.ProcessBuilder.Redirect
import models.MessageBoardModel


@Singleton
class MessageList1 @Inject()(cc: ControllerComponents) extends AbstractController(cc){
    def login = Action{ implicit request =>
        Ok(views.html.login1())
    }

    def validateLoginGet(username: String, password: String) = Action{
        Ok(s"$username logged in with $password.")
        Redirect(routes.MessageList1.messageBoard)
    }

    def validateLoginPost = Action { request =>
        val postVals = request.body.asFormUrlEncoded
        postVals.map { args =>
            val username = args("username").head
            val password = args("password").head
            if (MessageBoardModel.validateUser(username, password)){
                Redirect(routes.MessageList1.messageBoard).withSession("username" -> username)
            } else {
                Redirect(routes.MessageList1.login).flashing("error" -> "invalid username/password combination.")
            }
        }.getOrElse(Redirect(routes.MessageList1.login))
    }
    /*
    def messageBoard = Action{ implicit request =>
        val usernameOption = request.session.get("username")
        usernameOption.map { username =>
            val messages = MessageBoardModel.getMessages(username)
            Ok(views.html.messagelist1(messages))
        }.getOrElse(Redirect(routes.MessageList1.login)).flashing("error" -> "User creation failed.")
    }*/
    def messageBoard = Action { implicit request =>
        val usernameOption = request.session.get("username")
        usernameOption match {
            case Some(username) =>
            val personalMessages = MessageBoardModel.getMessages(username)
            val generalMessages = MessageBoardModel.getGenMessages()
            Ok(views.html.messagelist1(username, personalMessages, generalMessages))
            case None =>
            Redirect(routes.MessageList1.login)
        }
    }

    /*
    def messageBoard = Action { request =>
        val usernameOption = request.session.get("username")
        usernameOption match {
            case Some(username) =>
                val personalMessages = MessageBoardModel.getMessages(username)
                val generalMessages = MessageBoardModel.generalMessages
                Ok(views.html.messagelist1(username, personalMessages, generalMessages))
            case None =>
                Redirect(routes.MessageList1.login)
        }
    }*/

    def createUser = Action { request =>
        val postVals = request.body.asFormUrlEncoded
        postVals.map { args =>
            val username = args("username").head
            val password = args("password").head
            if (MessageBoardModel.createUser(username, password)){
                Redirect(routes.MessageList1.messageBoard).withSession("username" -> username)
            } else {
                Redirect(routes.MessageList1.login)
            }
        }.getOrElse(Redirect(routes.MessageList1.login))
    }

    def logout = Action{
        Redirect(routes.MessageList1.login).withNewSession
    }
/*
    def addMessage = Action{ implicit request =>
        val usernameOption = request.session.get("username")
        usernameOption.map{username =>
            val postVals = request.body.asFormUrlEncoded
            postVals.map { args =>
                val message = args("newMessage").head
                MessageBoardModel.addMessage(username, message)
                Redirect(routes.MessageList1.messageBoard)
            }.getOrElse(Redirect(routes.MessageList1.messageBoard))
        }.getOrElse(Redirect(routes.MessageList1.login))
    }*/

    def addMessage = Action { implicit request =>
        val usernameOption = request.session.get("username")
        val postVals = request.body.asFormUrlEncoded

        usernameOption match {
            case Some(username) =>
            postVals.map { args =>
                val message = args("newMessage").headOption.getOrElse("")
                MessageBoardModel.addMessage(username, None, message)
                Redirect(routes.MessageList1.messageBoard)
            }.getOrElse(Redirect(routes.MessageList1.messageBoard))
            case None =>
            Redirect(routes.MessageList1.login)
        }
    }

    def sendMessage = Action { implicit request =>
        val usernameOption = request.session.get("username")
        val form = request.body.asFormUrlEncoded

        usernameOption match {
            case Some(username) =>
                form.map { formData =>
                    val recipientUsername = formData("recipientUsername").headOption.getOrElse("")
                    val sentMessage = formData("sentMessage").headOption.getOrElse("")

                    // Check if the recipient exists in the users map
                    if (MessageBoardModel.getUsers().contains(recipientUsername)) {
                        // Add the message to the recipient's personal messages
                        MessageBoardModel.addMessage(username, Some(recipientUsername), sentMessage)
                        Redirect(routes.MessageList1.messageBoard)
                    } else {
                        Redirect(routes.MessageList1.messageBoard).flashing("error" -> "Recipient not found.")
                    }
                }.getOrElse {
                    Redirect(routes.MessageList1.messageBoard).flashing("error" -> "Invalid form data.")
                }
            case None =>
                Redirect(routes.MessageList1.login)
        }
    }


}