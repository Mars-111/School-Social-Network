const keycloak = new Keycloak({
    url: 'http://localhost:9082',
    realm: 'school-social-network',
    clientId: 'js-client'
});

function authenticationKeycloak() {
    return keycloak.init({ 
        onLoad: 'check-sso' 
    }).then(function(authenticated) {
        if (!authenticated) {
            return keycloak.login();
        }
        startTokenRefresh(); // Запускаем процесс обновления токена
        return Promise.resolve(authenticated);
    }).catch(function() {
        console.log('Failed to initialize');
        return Promise.reject();
    });
}


function startTokenRefresh() {
    // Устанавливаем интервал для обновления токена
    setInterval(() => {
        keycloak.updateToken(70) // Обновление токена, если он истекает через 70 секунд
            .then((refreshed) => {
                if (refreshed) {
                    console.log('Token refreshed');
                } else {
                    console.log('Token is still valid');
                }
            })
            .catch(() => {
                console.error('Failed to refresh token');
                // Здесь можно добавить логику для повторной аутентификации
            });
    }, 60000); // Проверяем каждые 60 секунд
}


export { keycloak, authenticationKeycloak };