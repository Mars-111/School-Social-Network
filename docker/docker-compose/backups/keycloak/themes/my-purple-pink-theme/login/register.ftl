<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>${msg("registerTitle","Регистрация")}</title>
    <style>
        body {
            background: linear-gradient(135deg, #8e2de2, #c200fb);
            font-family: 'Segoe UI', sans-serif;
            color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .register-container {
            background: rgba(51, 0, 102, 0.85);
            padding: 40px 30px;
            border-radius: 20px;
            box-shadow: 0 0 30px #ff5de7;
            width: 340px;
            animation: fadeIn 0.6s ease-in-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #ffffff;
        }

        .error-message {
            background-color: #ff4d6d;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            color: white;
            text-align: center;
        }

        label {
            font-weight: bold;
            font-size: 14px;
        }

        input[type=text], input[type=email], input[type=password] {
            width: 100%;
            padding: 10px;
            margin: 8px 0 20px;
            border: none;
            border-radius: 10px;
            outline: none;
            font-size: 16px;
            background-color: #fff;
            color: #330066;
        }

        button {
            background: #ff2f92;
            border: none;
            padding: 12px;
            width: 100%;
            border-radius: 10px;
            font-size: 18px;
            color: white;
            cursor: pointer;
            transition: background 0.3s ease;
        }

        button:hover {
            background: #c200fb;
        }
    </style>
</head>
<body>
<div class="register-container">
    <h2>Регистрация</h2>

    <#if message?has_content>
        <div class="error-message">${kcSanitize(message.summary)?no_esc}</div>
    </#if>

    <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
        <div class="${properties.kcFormGroupClass!}">
            <div class="${properties.kcLabelWrapperClass!}">
                <label for="username" class="${properties.kcLabelClass!}">Имя пользователя</label>
            </div>
            <div class="${properties.kcInputWrapperClass!}">
                <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" 
                       value="${(register.formData.username!'')}" type="text" 
                       autocomplete="username" required aria-invalid="<#if messagesPerField.existsError('username')>true</#if>" />
                <#if messagesPerField.existsError('username')>
                    <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('username'))?no_esc}
                    </span>
                </#if>
            </div>
        </div>

        <div class="${properties.kcFormGroupClass!}">
            <div class="${properties.kcLabelWrapperClass!}">
                <label for="email" class="${properties.kcLabelClass!}">Email</label>
            </div>
            <div class="${properties.kcInputWrapperClass!}">
                <input tabindex="2" id="email" class="${properties.kcInputClass!}" name="email" 
                       value="${(register.formData.email!'')}" type="email" 
                       autocomplete="email" required aria-invalid="<#if messagesPerField.existsError('email')>true</#if>" />
                <#if messagesPerField.existsError('email')>
                    <span id="input-error-email" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('email'))?no_esc}
                    </span>
                </#if>
            </div>
        </div>

        <#if passwordRequired??>
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="password" class="${properties.kcLabelClass!}">Пароль</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input tabindex="3" id="password" class="${properties.kcInputClass!}" name="password" 
                           type="password" autocomplete="new-password" required 
                           aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>" />
                    <#if messagesPerField.existsError('password')>
                        <span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('password'))?no_esc}
                        </span>
                    </#if>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="password-confirm" class="${properties.kcLabelClass!}">Подтвердите пароль</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input tabindex="4" id="password-confirm" class="${properties.kcInputClass!}" name="password-confirm" 
                           type="password" autocomplete="new-password" required 
                           aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>" />
                    <#if messagesPerField.existsError('password-confirm')>
                        <span id="input-error-password-confirm" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                        </span>
                    </#if>
                </div>
            </div>
        </#if>

        <#if recaptchaRequired??>
            <div class="form-group">
                <div class="${properties.kcInputWrapperClass!}">
                    <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                </div>
            </div>
        </#if>

        <div class="${properties.kcFormGroupClass!}">
            <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                <div class="${properties.kcFormOptionsWrapperClass!}">
                    <span><a href="${url.loginUrl}">« Назад к входу</a></span>
                </div>
            </div>

            <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" 
                       type="submit" value="Зарегистрироваться"/>
            </div>
        </div>
    </form>
</div>
</body>
</html>