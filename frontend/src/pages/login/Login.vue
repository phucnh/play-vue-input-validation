<template>
  <div id="app">
    <h1>Login Page</h1>
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
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import Axios from 'axios';
import { ValidationObserver, ValidationProvider } from 'vee-validate';

type ObserverInstance = InstanceType<typeof ValidationObserver>;

export default Vue.extend({
  name: 'Login',
  components: {
    ValidationObserver,
    ValidationProvider,
  },
  data: () => {
    return {
      username: null,
      password: null,
    };
  },
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
});
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
