document.addEventListener("DOMContentLoaded", function () {
    const eyeIcons = document.querySelectorAll(".toggle-password");
  
    eyeIcons.forEach(icon => {
      icon.addEventListener("click", function () {
        const targetId = icon.getAttribute("data-target");
        const input = document.getElementById(targetId);
  
        if (input.type === "password") {
          input.type = "text";
          icon.src = "images/eye2.png"; 
        } else {
          input.type = "password";
          icon.src = "images/eye.png"; 
        }
      });
    });
  });
  

document.getElementById("update-btn").addEventListener("click", async function () {
    const currentPassword = document.getElementById("current-password").value.trim();
    const newPassword = document.getElementById("new-password").value.trim();
    const confirmNewPassword = document.getElementById("new2-password").value.trim();

    // Frontend doğrulama
    if (!currentPassword) {
        showToastMessage("Mevcut şifre boş bırakılamaz.", false);
        return;
    }

    if (!newPassword) {
        showToastMessage("Yeni şifre boş bırakılamaz."), false;
        return;
    }

    if (newPassword.length < 10) {
        showToastMessage("Yeni şifre en az 10 karakter olmalıdır.", false);
        return;
    }

    if (!confirmNewPassword) {
        showToastMessage("Yeni şifre tekrarı boş bırakılamaz.", false);
        return;
    }

    if (newPassword !== confirmNewPassword) {
        showToastMessage("Yeni şifreler uyuşmuyor.", false);
        return;
    }

    const data = {
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmNewPassword: confirmNewPassword
    };

    try {
        const response = await fetch("https://localhost:8443/users/change-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(data)
        });

        const result = await response.json();

        if (result.result === true) {
            showToastMessage("Şifreniz başarıyla değiştirildi. Lütfen tekrar giriş yapınız.",true);
            // 2 saniye sonra login sayfasına yönlendir
            setTimeout(() => {
                window.location.href = "login.html";
            }, 2000);
        } else {
            showToastMessage(result.errorMessage, false);
        }

    } catch (error) {
        console.error("Sunucu hatası:", error);
        showToastMessage("Beklenmeyen bir hata oluştu.", false);
    }
});