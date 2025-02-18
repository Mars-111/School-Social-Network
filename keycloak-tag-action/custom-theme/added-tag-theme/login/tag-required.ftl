<!DOCTYPE html>
<html lang="${locale}">
<head>
    <title>${msg("registerTitle")}</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/login.css">
</head>
<body>
<div id="kc-register-form">
    <form id="kc-register-form" action="${url.registrationAction}" method="post">
        <div class="form-group">
            <label for="tag">${msg("tag", "Tag")}</label>
            <input type="text" id="tag" name="tag" class="form-control" />
        </div>
        <#-- Остальные поля формы -->
        <#include "register-form-username.ftl" />
        <#include "register-form-password.ftl" />

        <div class="form-group">
            <button type="submit" class="btn btn-primary">${msg("doRegister")}</button>
        </div>
    </form>
</div>
</body>
</html>
