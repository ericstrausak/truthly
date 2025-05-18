<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";

  // get the origin of current page, e.g. http://localhost:8080
  const api_root = page.url.origin;

  let companies = $state([]);
  let jobs = $state([]);
  let job = $state({
    title: null,
    description: null,
    earnings: null,
    jobType: null,
    companyId: null,
  });

  onMount(() => {
    getCompanies();
    getJobs();
  });

  function getCompanies() {
    var config = {
      method: "get",
      url: api_root + "/api/company",
      headers: {},
    };

    axios(config)
      .then(function (response) {
        companies = response.data;
      })
      .catch(function (error) {
        alert("Could not get companies");
        console.log(error);
      });
  }

  function getJobs() {
    var config = {
      method: "get",
      url: api_root + "/api/job",
      headers: {},
    };

    axios(config)
      .then(function (response) {
        jobs = response.data;
      })
      .catch(function (error) {
        alert("Could not get jobs");
        console.log(error);
      });
  }

  function createJob() {
    var config = {
      method: "post",
      url: api_root + "/api/job",
      headers: {
        "Content-Type": "application/json",
      },
      data: job,
    };

    axios(config)
      .then(function (response) {
        alert("Job created");
        getJobs();
      })
      .catch(function (error) {
        alert("Could not create Job");
        console.log(error);
      });
  }
</script>

<h1 class="mt-3">Create Job</h1>
<form class="mb-5">
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="description">Title</label>
      <input
        bind:value={job.title}
        class="form-control"
        id="description"
        type="text"
      />
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="description">Description</label>
      <input
        bind:value={job.description}
        class="form-control"
        id="description"
        type="text"
      />
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="type">Type</label>
      <select bind:value={job.jobType} class="form-select" id="type">
        <option value="OTHER">OTHER</option>
        <option value="TEST">TEST</option>
        <option value="IMPLEMENT">IMPLEMENT</option>
        <option value="REVIEW">REVIEW</option>
      </select>
    </div>
    <div class="col">
      <label class="form-label" for="earnings">Earnings</label>
      <input
        bind:value={job.earnings}
        class="form-control"
        id="earnings"
        type="number"
      />
    </div>
    <div class="col">
      <label class="form-label" for="company">Company</label>
      <select bind:value={job.companyId} class="form-select" id="company">
        {#each companies as company}
          <option value={company.id}>{company.name}</option>
        {/each}
      </select>
    </div>
  </div>
  <button type="button" class="btn btn-primary" onclick={createJob}
    >Submit</button
  >
</form>

<h1>All Jobs</h1>
<table class="table">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Description</th>
      <th scope="col">Type</th>
      <th scope="col">Earnings</th>
      <th scope="col">State</th>
      <th scope="col">CompanyId</th>
    </tr>
  </thead>
  <tbody>
    {#each jobs as job}
      <tr>
        <td>{job.title}</td>
        <td>{job.description}</td>
        <td>{job.jobType}</td>
        <td>{job.earnings}</td>
        <td>{job.jobState}</td>
        <td>{job.companyId}</td>
      </tr>
    {/each}
  </tbody>
</table>
