<!DOCTYPE html>
<html>
<head>
    <title>FREESPACE</title>
</head>
<body>
<h1>SSN</h1><hr>
<#if message??>
    <h2 style="color: green">${message}</h2>
</#if>
<h4>Регистрация</h4>
<form action="/login" method="post">
    Имя пользователя: <input type="text" name="username"><br><br>
    Пароль: <input type="password" name="password"><br><br>
    <input type="hidden" name="_csrf" value="${_csrf.token}">
    <input type="submit" value="Регистрация"/>
</form>
<br>
<#if errorMessage??>
    <h2 style="color: red">${errorMessage}</h2>
</#if>
<br>
<p>У вас уже есть аккаунт? <a href="/login">Войти</a></p>
</body>
</html>