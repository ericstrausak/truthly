<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";

  // get the origin of current page, e.g. http://localhost:8080
  const api_root = page.url.origin;

  let users = $state([]);
  let user = $state({
    username: null,
    email: null,
    password: null,
    role: null,
  });

  onMount(() => {
    getUsers();
  });

  function getUsers() {
    var config = {
      method: "get",
      url: api_root + "/api/user",
      headers: {},
    };

    axios(config)
      .then(function (response) {
        users = response.data;
      })
      .catch(function (error) {
        alert("Could not get users");
        console.log(error);
      });
  }

  function createUser() {
    var config = {
      method: "post",
      url: api_root + "/api/user",
      headers: {
        "Content-Type": "application/json",
      },
      data: user,
    };

    axios(config)
      .then(function (response) {
        alert("User created");
        getUsers();
        // Clear form
        user = {
          username: null,
          email: null,
          password: null,
          role: null,
        };
      })
      .catch(function (error) {
        alert("Could not create User");
        console.log(error);
      });
  }
</script>

<h1 class="mt-3">Create User</h1>
<form class="mb-5">
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="username">Username</label>
      <input
        bind:value={user.username}
        class="form-control"
        id="username"
        type="text"
      />
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="email">Email</label>
      <input
        bind:value={user.email}
        class="form-control"
        id="email"
        type="email"
      />
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="password">Password</label>
      <input
        bind:value={user.password}
        class="form-control"
        id="password"
        type="password"
      />
    </div>
    <div class="col">
      <label class="form-label" for="role">Role</label>
      <select bind:value={user.role} class="form-select" id="role">
        <option value="">Select Role</option>
        <option value="USER">User</option>
        <option value="FACT_CHECKER">Fact Checker</option>
        <option value="ADMIN">Admin</option>
      </select>
    </div>
  </div>
  <button type="button" class="btn btn-primary" onclick={createUser}
    >Submit</button
  >
</form>

<h1>All Users</h1>
<table class="table">
  <thead>
    <tr>
      <th scope="col">Username</th>
      <th scope="col">Email</th>
      <th scope="col">Role</th>
      <th scope="col">Registration Date</th>
    </tr>
  </thead>
  <tbody>
    {#each users as userItem}
      <tr>
        <td>{userItem.username}</td>
        <td>{userItem.email}</td>
        <td>{userItem.role}</td>
        <td>{new Date(userItem.registrationDate).toLocaleDateString()}</td>
      </tr>
    {/each}
  </tbody>
</table>