function logout() {
    fetch('https://localhost:8443/auth/logout', {
        method: 'POST',
        credentials: 'include', 
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json()) 
    .then(data => {
        if (data.result) { 
            showToastMessage(data.data || "Çıkış Yaptınız", true);

            // 1.5 saniye sonra yönlendir 
            setTimeout(() => {
                window.location.href = "login.html"; 
            }, 1500);
        } else {
            showToastMessage(data.errorMessage, false);
        }
    })
    .catch(error => {
        console.error('Çıkış işlemi sırasında hata:', error);
        showToastMessage('Çıkış işlemi sırasında bir hata oluştu.', false);
    });
}


document.getElementById('logoutButton').addEventListener('click', logout);
