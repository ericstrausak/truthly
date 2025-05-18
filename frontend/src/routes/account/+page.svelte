<script>
  import { user } from "../../store";
</script>

{#if $user}
  <h1>Account Details</h1>
  <p><strong>Name:</strong> {$user.name}</p>
  <p><strong>Email:</strong> {$user.email}</p>
  <p><strong>Nickname:</strong> {$user.nickname}</p>
  <!-- Add more user details as needed -->
{/if}