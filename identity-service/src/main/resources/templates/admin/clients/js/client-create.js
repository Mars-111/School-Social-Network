const form = document.getElementById('create-client-form');
const successMessage = document.getElementById('success-message');

form.addEventListener('submit', async function (e) {
    e.preventDefault();

    // Очистка старых сообщений об ошибках
    document.querySelectorAll('.error').forEach(div => div.textContent = '');
    successMessage.textContent = '';

    // Получение данных из формы
    const formData = {
        name: form.name.value.trim(),
        description: form.description.value.trim(),
        secret: form.secret.value,
        allowedRedirectUris: form.allowedRedirectUris.value.split(',').map(s => s.trim()).filter(s => s.length > 0)
    };

    try {
        const response = await fetch('/admin/api/clients', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            form.reset(); // Очистить форму
            successMessage.textContent = 'Client created successfully!';
        } else {
            successMessage.textContent = 'Network error. Please try again.';
        }
    } catch (error) {
        if (error.textContent) {
            successMessage.textContent = 'Error: ' + error.textContent;
        }
        else {
            successMessage.textContent = 'Network error. Please try again.';
        }
        successMessage.style.color = 'red';
    }
});