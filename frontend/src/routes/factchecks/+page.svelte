<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";

  const api_root = page.url.origin;

  let articles = $state([]);
  let users = $state([]);
  let factChecks = $state([]);
  let factCheck = $state({
    articleId: null,
    checkerId: null,
    result: null,
    description: null,
  });

  onMount(() => {
    getArticles();
    getUsers();
    getFactChecks();
  });

  function getArticles() {
    var config = {
      method: "get",
      url: api_root + "/api/article",
      headers: {},
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

  function getFactChecks() {
    // Since you don't have a GET endpoint for fact checks yet,
    // this will likely fail - you might want to add that endpoint
    var config = {
      method: "get",
      url: api_root + "/api/factcheck",
      headers: {},
    };

    axios(config)
      .then(function (response) {
        factChecks = response.data;
      })
      .catch(function (error) {
        console.log("Could not get fact checks - endpoint might not exist yet");
        factChecks = []; // Set to empty array if endpoint doesn't exist
      });
  }

  function createFactCheck() {
    var config = {
      method: "post",
      url: api_root + "/api/factcheck",
      headers: {
        "Content-Type": "application/json",
      },
      data: factCheck,
    };

    axios(config)
      .then(function (response) {
        alert("Fact check created");
        getFactChecks(); // Refresh the list
        // Clear form
        factCheck = {
          articleId: null,
          checkerId: null,
          result: null,
          description: null,
        };
      })
      .catch(function (error) {
        alert("Could not create fact check");
        console.log(error);
      });
  }

  // Helper function to get article title by ID
  function getArticleTitle(articleId) {
    const article = articles.find(a => a.id === articleId);
    return article ? article.title : 'Unknown Article';
  }

  // Helper function to get user name by ID
  function getUserName(userId) {
    const user = users.find(u => u.id === userId);
    return user ? user.username : 'Unknown User';
  }
</script>

<h1 class="mt-3">Create Fact Check</h1>
<form class="mb-5">
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="article">Article</label>
      <select bind:value={factCheck.articleId} class="form-select" id="article">
        <option value="">Select Article</option>
        {#each articles as article}
          <option value={article.id}>{article.title}</option>
        {/each}
      </select>
    </div>
    <div class="col">
      <label class="form-label" for="checker">Fact Checker</label>
      <select bind:value={factCheck.checkerId} class="form-select" id="checker">
        <option value="">Select Checker</option>
        {#each users.filter(user => user.role === 'FACT_CHECKER' || user.role === 'ADMIN') as user}
          <option value={user.id}>{user.username} ({user.role})</option>
        {/each}
      </select>
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="result">Result</label>
      <select bind:value={factCheck.result} class="form-select" id="result">
        <option value="">Select Result</option>
        <option value="TRUE">True</option>
        <option value="FALSE">False</option>
        <option value="PARTLY_TRUE">Partly True</option>
      </select>
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="description">Description</label>
      <textarea
        bind:value={factCheck.description}
        class="form-control"
        id="description"
        rows="4"
        placeholder="Explain the fact check result..."
      ></textarea>
    </div>
  </div>
  <button type="button" class="btn btn-primary" onclick={createFactCheck}
    >Submit Fact Check</button
  >
</form>

<h1>All Fact Checks</h1>
{#if factChecks.length > 0}
  <table class="table">
    <thead>
      <tr>
        <th scope="col">Article</th>
        <th scope="col">Checker</th>
        <th scope="col">Result</th>
        <th scope="col">Description</th>
        <th scope="col">Check Date</th>
      </tr>
    </thead>
    <tbody>
      {#each factChecks as factCheckItem}
        <tr>
          <td>{getArticleTitle(factCheckItem.articleId)}</td>
          <td>{getUserName(factCheckItem.checkerId)}</td>
          <td>
            <span class="badge bg-{factCheckItem.result === 'TRUE' ? 'success' : factCheckItem.result === 'FALSE' ? 'danger' : 'warning'}">
              {factCheckItem.result}
            </span>
          </td>
          <td>{factCheckItem.description}</td>
          <td>{new Date(factCheckItem.checkDate).toLocaleDateString()}</td>
        </tr>
      {/each}
    </tbody>
  </table>
{:else}
  <div class="alert alert-info">
    <p>No fact checks available yet. Create your first fact check above!</p>
    <small class="text-muted">
      Note: The fact check list endpoint might not be implemented yet. 
      You can still create fact checks, but they won't appear in this table until you add a GET /api/factcheck endpoint.
    </small>
  </div>
{/if}