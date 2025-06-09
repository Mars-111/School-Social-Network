<#import "template.ftl" as layout>

<@layout.registrationLayout displayMessage=messagesPerField.exists('global'); section>

    <#if section = "header">
        <h2>${msg("enterYourTag", "Введите свой тег")}</h2>

    <#elseif section = "form">
        <form action="${url.loginAction}" method="post">

            <div class="form-group">
                <label for="tag">${msg("enterTag", "Введите свой тег:")}</label>
                <input type="text" id="tag" name="tag" class="form-control"
                       value="<#if formData?? && formData.tag??>${kcSanitize(formData.tag)}</#if>"
                       required
                       aria-invalid="<#if message?has_content>true</#if>"/>

                <#if message?has_content>
                    <span class="error">${kcSanitize(message)?no_esc}</span>
                </#if>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">${msg("submit", "Отправить")}</button>
            </div>

        </form>

    </#if>

</@layout.registrationLayout>
