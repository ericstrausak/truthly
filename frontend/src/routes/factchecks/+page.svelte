<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";
  import { jwt_token } from "../../store";

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

  // AI-related state
  let aiVerificationResults = $state(new Map());
  let loadingAI = $state(new Set());
  let selectedArticleForAI = $state("");

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
    var config = {
      method: "get",
      url: api_root + "/api/factcheck",
      headers: { Authorization: "Bearer " + $jwt_token },
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

  // AI Verification Function
  async function performAIVerification(articleId) {
    if (!articleId) {
      alert("Please select an article first");
      return;
    }

    loadingAI.add(articleId);
    loadingAI = new Set(loadingAI);

    try {
      const config = {
        method: "post",
        url: api_root + `/api/factcheck/ai-verify/${articleId}`,
        headers: {
          "Content-Type": "application/json",
        },
      };

      const response = await axios(config);
      aiVerificationResults.set(articleId, response.data);
      aiVerificationResults = new Map(aiVerificationResults);
      
      // Show success message
      const article = articles.find(a => a.id === articleId);
      alert(`🤖 AI verification completed for "${article?.title}"\nRating: ${response.data.aiRating}`);
      
    } catch (error) {
      alert("❌ AI verification failed: " + error.message);
      console.log(error);
    } finally {
      loadingAI.delete(articleId);
      loadingAI = new Set(loadingAI);
    }
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
        alert("✅ Fact check created successfully!");
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
        alert("❌ Could not create fact check");
        console.log(error);
      });
  }

  // Enhanced createFactCheck with AI pre-verification
  async function createFactCheckWithAI() {
    if (!factCheck.articleId) {
      alert("Please select an article first");
      return;
    }
    
    // Perform AI verification first if not already done
    if (!aiVerificationResults.has(factCheck.articleId)) {
      await performAIVerification(factCheck.articleId);
    }
    
    // Then create the fact check
    createFactCheck();
  }

  // Helper function to get article title by ID
  function getArticleTitle(articleId) {
    const article = articles.find((a) => a.id === articleId);
    return article ? article.title : "Unknown Article";
  }

  // Helper function to get user name by ID
  function getUserName(userId) {
    const user = users.find((u) => u.id === userId);
    return user ? user.username : "Unknown User";
  }

  // Helper function for badge colors
  function getRatingBadgeClass(rating) {
    switch(rating) {
      case 'TRUE': return 'bg-success';
      case 'FALSE': return 'bg-danger';
      case 'PARTLY_TRUE': return 'bg-warning';
      case 'UNVERIFIABLE': return 'bg-secondary';
      default: return 'bg-secondary';
    }
  }
</script>

<h1 class="mt-3">🤖 AI-Powered Fact Checking</h1>

<!-- AI Verification Section -->
<div class="card mb-4">
  <div class="card-header bg-info text-white">
    <h5 class="mb-0">
      <i class="fas fa-robot"></i> Step 1: AI Pre-Verification
    </h5>
  </div>
  <div class="card-body">
    <div class="row mb-3">
      <div class="col-8">
        <label class="form-label" for="ai-article">Select Article for AI Analysis</label>
        <select bind:value={selectedArticleForAI} class="form-select" id="ai-article">
          <option value="">Choose an article to analyze...</option>
          {#each articles as article}
            <option value={article.id}>{article.title}</option>
          {/each}
        </select>
      </div>
      <div class="col-4 d-flex align-items-end">
        <button 
          type="button" 
          class="btn btn-info w-100"
          onclick={() => performAIVerification(selectedArticleForAI)}
          disabled={!selectedArticleForAI || loadingAI.has(selectedArticleForAI)}
        >
          {#if loadingAI.has(selectedArticleForAI)}
            <span class="spinner-border spinner-border-sm me-2"></span>
            AI Analyzing...
          {:else}
            🤖 Verify with AI
          {/if}
        </button>
      </div>
    </div>
    
    <!-- AI Results Display -->
    {#if selectedArticleForAI && aiVerificationResults.has(selectedArticleForAI)}
      {@const aiResult = aiVerificationResults.get(selectedArticleForAI)}
      <div class="alert alert-light border">
        <h6><i class="fas fa-robot text-info"></i> AI Verification Result</h6>
        <div class="row">
          <div class="col-md-3">
            <strong>Article:</strong><br>
            <small class="text-muted">{aiResult.articleTitle}</small>
          </div>
          <div class="col-md-2">
            <strong>AI Rating:</strong><br>
            <span class="badge {getRatingBadgeClass(aiResult.aiRating)}">
              {aiResult.aiRating}
            </span>
          </div>
          <div class="col-md-7">
            <strong>AI Explanation:</strong><br>
            <small>{aiResult.aiExplanation}</small>
          </div>
        </div>
        <hr>
        <small class="text-muted">
          <i class="fas fa-clock"></i> Verified: {new Date(aiResult.verificationDate).toLocaleString()}
        </small>
      </div>
    {/if}
  </div>
</div>

<!-- Human Fact Check Section -->
<div class="card mb-5">
  <div class="card-header bg-primary text-white">
    <h5 class="mb-0">
      <i class="fas fa-user-check"></i> Step 2: Human Fact Check
    </h5>
  </div>
  <div class="card-body">
    <form>
      <div class="row mb-3">
        <div class="col">
          <label class="form-label" for="article">Article to Check</label>
          <select bind:value={factCheck.articleId} class="form-select" id="article">
            <option value="">Select Article</option>
            {#each articles as article}
              <option value={article.id}>
                {article.title}
                {#if aiVerificationResults.has(article.id)}
                  (AI: {aiVerificationResults.get(article.id).aiRating})
                {/if}
              </option>
            {/each}
          </select>
        </div>
        <div class="col">
          <label class="form-label" for="checker">Fact Checker</label>
          <select bind:value={factCheck.checkerId} class="form-select" id="checker">
            <option value="">Select Checker</option>
            {#each users.filter((user) => user.role === "FACT_CHECKER" || user.role === "ADMIN") as user}
              <option value={user.id}>{user.username} ({user.role})</option>
            {/each}
          </select>
        </div>
      </div>
      
      <div class="row mb-3">
        <div class="col">
          <label class="form-label" for="result">Human Rating</label>
          <select bind:value={factCheck.result} class="form-select" id="result">
            <option value="">Select Result</option>
            <option value="TRUE">✅ True</option>
            <option value="FALSE">❌ False</option>
            <option value="PARTLY_TRUE">⚠️ Partly True</option>
            <option value="UNVERIFIABLE">❓ Unverifiable</option>
          </select>
        </div>
        
        <!-- AI Suggestion Display -->
        {#if factCheck.articleId && aiVerificationResults.has(factCheck.articleId)}
          <div class="col">
            <label class="form-label">AI Suggestion</label>
            <div class="alert alert-info py-2">
              <small>
                <strong>🤖 AI suggests:</strong> 
                <span class="badge {getRatingBadgeClass(aiVerificationResults.get(factCheck.articleId).aiRating)}">
                  {aiVerificationResults.get(factCheck.articleId).aiRating}
                </span>
              </small>
            </div>
          </div>
        {/if}
      </div>
      
      <div class="row mb-3">
        <div class="col">
          <label class="form-label" for="description">Human Explanation</label>
          <textarea
            bind:value={factCheck.description}
            class="form-control"
            id="description"
            rows="4"
            placeholder="Provide your detailed fact-check explanation..."
          ></textarea>
        </div>
      </div>
      
      <div class="d-flex gap-2">
        <button type="button" class="btn btn-primary" onclick={createFactCheck}>
          <i class="fas fa-check"></i> Submit Manual Check
        </button>
        <button type="button" class="btn btn-success" onclick={createFactCheckWithAI}>
          <i class="fas fa-robot"></i> AI + Human Check
        </button>
      </div>
    </form>
  </div>
</div>

<!-- Fact Checks Table -->
<h2>All Fact Checks</h2>
{#if factChecks.length > 0}
  <table class="table table-striped">
    <thead class="table-dark">
      <tr>
        <th scope="col">Article</th>
        <th scope="col">Checker</th>
        <th scope="col">Human Result</th>
        <th scope="col">AI Result</th>
        <th scope="col">Description</th>
        <th scope="col">Check Date</th>
        <th scope="col">Actions</th>
      </tr>
    </thead>
    <tbody>
      {#each factChecks as factCheckItem}
        <tr>
          <td>
            <strong>{getArticleTitle(factCheckItem.articleId)}</strong>
          </td>
          <td>{getUserName(factCheckItem.checkerId)}</td>
          <td>
            <span class="badge {getRatingBadgeClass(factCheckItem.result)}">
              {factCheckItem.result}
            </span>
          </td>
          <td>
            {#if factCheckItem.aiVerificationResult}
              <span class="badge {getRatingBadgeClass(factCheckItem.aiVerificationResult)}">
                {factCheckItem.aiVerificationResult}
              </span>
            {:else}
              <span class="badge bg-secondary">No AI</span>
            {/if}
          </td>
          <td>
            <small>{factCheckItem.description}</small>
            {#if factCheckItem.aiExplanation}
              <br><small class="text-muted">🤖 AI: {factCheckItem.aiExplanation.substring(0, 100)}...</small>
            {/if}
          </td>
          <td>{new Date(factCheckItem.checkDate).toLocaleDateString()}</td>
          <td>
            <button 
              class="btn btn-sm btn-outline-info" 
              onclick={() => performAIVerification(factCheckItem.articleId)}
              disabled={loadingAI.has(factCheckItem.articleId)}
            >
              {#if loadingAI.has(factCheckItem.articleId)}
                <span class="spinner-border spinner-border-sm"></span>
              {:else}
                🤖 Re-verify
              {/if}
            </button>
          </td>
        </tr>
      {/each}
    </tbody>
  </table>
{:else}
  <div class="alert alert-info">
    <h5><i class="fas fa-info-circle"></i> No fact checks yet</h5>
    <p>Create your first AI-powered fact check above!</p>
    <small class="text-muted">
      The AI will analyze articles for suspicious content, misinformation patterns, and factual accuracy.
    </small>
  </div>
{/if}