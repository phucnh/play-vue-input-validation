Server Side Validation with Play Framework, Vue.js, and VeeValidate
---

- [Play Framework](https://github.com/playframework/playframework)
- [Vue.js](http://vuejs.org/)
- [VeeValidate](https://logaretm.github.io/vee-validate/)

## Prerequisites

- sbt
- yarn

## Running

```sh
sbt run
```

## Explanation

### Bind POST data to Play Framework Form

We have following form with [some constraints](https://www.playframework.com/documentation/2.8.x/ScalaForms#Defining-constraints-on-the-form)

```scala
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
```

The POST data will be

```json
{ "username": "xxx", "password": "yyy" }
```

We can bind the data to Play Framework form as follows:

```scala
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
```

When validation failed, the errors are:

```json
{"username":["This field is required"],"password":["This field is required"]}
```

### Display validation errors with Vue.js and VeeValidate

```vue
<ValidationObserver ref="loginFormValidationOb">
  Username:
  <ValidationProvider name="username" v-slot="{ errors }">
    <input type="text" name="username" id="username" v-model="username" />
    <span>{{ errors[0] }}</span>
  </ValidationProvider>
    <br />
  Password:
  <ValidationProvider name="password" v-slot="{ errors }">
    <input type="text" name="password" id="password" v-model="password" />
    <span>{{ errors[0] }}</span>
  </ValidationProvider>
  <br />
</ValidationObserver>
<button @click.prevent="login">Login</button>
```

The `@click.prevent="login"` implementation is:

```typescript
  ...
  methods: {
    login() {
      Axios.post('/login', {
        username: this.username,
        password: this.password,
      }).catch((error) => {
        // https://logaretm.github.io/vee-validate/guide/forms.html#programmatic-access-with-refs
        (this.$refs.loginFormValidationOb as ObserverInstance).setErrors(
          error.response?.data,
        );
      });
    },
  },
```

See [VeeValidate: Handling Backend Validation](https://logaretm.github.io/vee-validate/advanced/server-side-validation.html#handling-backend-validation) for more information.