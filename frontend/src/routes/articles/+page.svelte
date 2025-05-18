<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";
  import { jwt_token } from "../../store";

  // get the origin of current page, e.g. http://localhost:8080
  const api_root = page.url.origin;

  let users = $state([]);
  let articles = $state([]);
  let article = $state({
    title: null,
    content: null,
    authorId: null,
    isAnonymous: false,
  });

  onMount(() => {
    getUsers();
    getArticles();
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

  function getArticles() {
    var config = {
      method: "get",
      url: api_root + "/api/article",
      headers: {Authorization: "Bearer " + $jwt_token},
    };

    axios(config)
      .then(function (response) {
        articles = response.data;
      })
      .catch(function (error) {
        alert("Could not get articles");
        console.log(error);
      });
  }

  function createArticle() {
    var config = {
      method: "post",
      url: api_root + "/api/article",
      headers: {
        "Content-Type": "application/json",
      },
      data: article,
    };

    axios(config)
      .then(function (response) {
        alert("Article created");
        getArticles();
        // Clear form
        article = {
          title: null,
          content: null,
          authorId: null,
          isAnonymous: false,
        };
      })
      .catch(function (error) {
        alert("Could not create Article");
        console.log(error);
      });
  }
</script>

<h1 class="mt-3">Create Article</h1>
<form class="mb-5">
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="title">Title</label>
      <input
        bind:value={article.title}
        class="form-control"
        id="title"
        type="text"
      />
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="content">Content</label>
      <textarea
        bind:value={article.content}
        class="form-control"
        id="content"
        rows="5"
      ></textarea>
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="author">Author</label>
      <select bind:value={article.authorId} class="form-select" id="author">
        <option value="">Select Author</option>
        {#each users as user}
          <option value={user.id}>{user.username}</option>
        {/each}
      </select>
    </div>
    <div class="col">
      <label class="form-label" for="anonymous">Anonymous</label>
      <div class="form-check">
        <input
          bind:checked={article.isAnonymous}
          class="form-check-input"
          id="anonymous"
          type="checkbox"
        />
        <label class="form-check-label" for="anonymous"> Publish Anonymously </label>
      </div>
    </div>
  </div>
  <button type="button" class="btn btn-primary" onclick={createArticle}
    >Submit</button
  >
</form>

<h1>All Articles</h1>
<table class="table">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Content</th>
      <th scope="col">Author ID</th>
      <th scope="col">Status</th>
      <th scope="col">Anonymous</th>
      <th scope="col">Publication Date</th>
    </tr>
  </thead>
  <tbody>
    {#each articles as articleItem}
      <tr>
        <td>{articleItem.title}</td>
        <td>{articleItem.content.substring(0, 100)}...</td>
        <td>{articleItem.authorId}</td>
        <td>{articleItem.status}</td>
        <td>{articleItem.isAnonymous ? 'Yes' : 'No'}</td>
        <td>{new Date(articleItem.publicationDate).toLocaleDateString()}</td>
      </tr>
    {/each}
  </tbody>
</table>