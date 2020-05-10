package controllers

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.libs.json.{JsError, Json}
import play.api.libs.json._
import play.api.data.Forms._
import play.api.mvc._

@Singleton
class LoginController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController
  with play.api.i18n.I18nSupport {
  import LoginController._

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

  def login() = Action { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        loginForm.bind(json).fold(
          errors => BadRequest(errors.errorsAsJson),
          form => Ok
        )
      case None =>
        BadRequest
    }
  }
}

object LoginController {
  case class LoginForm(
    username: String,
    password: String
  )

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText(minLength = 4),
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  )
}
