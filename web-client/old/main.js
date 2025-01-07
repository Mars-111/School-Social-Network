document.addEventListener("DOMContentLoaded", function() {
  // Initialize Keycloak
  const keycloak = new Keycloak({
      url: 'http://localhost:9082',
      realm: 'school-social-network',
      clientId: 'js-client'
  });

  const outputTextarea = document.getElementById('output');

  function logToTextarea(message) {
      const now = new Date();
      const timestamp = now.toLocaleString();
      outputTextarea.value += `[${timestamp}] ${message}\n`;
  }

  keycloak.init({ 
      onLoad: 'check-sso' 
  }).then(function(authenticated) {
      logToTextarea(authenticated ? 'User is authenticated' : 'User is not authenticated');

      document.getElementById('loginBtn').addEventListener('click', function() {
          logToTextarea('Login button clicked');
          keycloak.login();
      });

      document.getElementById('logoutBtn').addEventListener('click', function() {
          logToTextarea('Logout button clicked');
          keycloak.logout();
      });

      document.getElementById('isLoggedInBtn').addEventListener('click', function() {
          const isLoggedInMessage = keycloak.authenticated ? 'User is logged in' : 'User is not logged in';
          logToTextarea('Is Logged In button clicked: ' + isLoggedInMessage);
          alert(isLoggedInMessage);
      });

      document.getElementById('accessTokenBtn').addEventListener('click', function() {
          if (keycloak.authenticated) {
              logToTextarea('Access Token button clicked: ' + keycloak.token);
              alert('Access Token: ' + keycloak.token);
          } else {
              const notLoggedInMessage = 'User is not logged in';
              logToTextarea('Access Token button clicked: ' + notLoggedInMessage);
              alert(notLoggedInMessage);
          }
      });

      document.getElementById('showParsedTokenBtn').addEventListener('click', function() {
          if (keycloak.authenticated) {
              const parsedToken = keycloak.tokenParsed;
              logToTextarea('Show Parsed Access Token button clicked: ' + JSON.stringify(parsedToken, null, 2));
              alert('Parsed Access Token: ' + JSON.stringify(parsedToken, null, 2));
          } else {
              const notLoggedInMessage = 'User is not logged in';
              logToTextarea('Show Parsed Access Token button clicked: ' + notLoggedInMessage);
              alert(notLoggedInMessage);
          }
      });



      async function getUserById(username, accessToken) {
        const url = `http://localhost:9082/admin/realms/school-social-network/users?username=${username}&exact=true`;
    
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            }
        });
    
        if (response.ok) {
            const userInfo = await response.json();
            console.log(userInfo);
            return userInfo;
        } else {
            console.error(`Failed to fetch user info, status code: ${response.status}`);
            const errorInfo = await response.json();
            console.error(errorInfo);
            return null;
        }
    }

      document.getElementById('GetUserById').addEventListener('click', function() {
          logToTextarea('GetUserById- > ' + keycloak.username);

          if (keycloak.authenticated) {
              getUserById(keycloak.username, keycloak.token)
                .then(userInfo => {
                    if (userInfo) {
                        // Делайте что-то с информацией о пользователе
                        console.log('User Info:', userInfo);
                    }
                })
                .catch(error => {
                    console.error('Error fetching user info:', error);
                });
            
          } else {
              const notLoggedInMessage = 'User is not logged in';
              logToTextarea('API call failed: ' + notLoggedInMessage);
              alert(notLoggedInMessage);
          }
      });



  }).catch(function() {
      console.log('Failed to initialize');
  });
});