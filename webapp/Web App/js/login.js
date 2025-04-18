document.addEventListener("DOMContentLoaded", function () {
    const passwordInput = document.getElementById("password");
    const eyeIcon = document.getElementById("eye-icon");
  
    eyeIcon.addEventListener("click", function () {
      if (passwordInput.type === "password") {
        passwordInput.type = "text";
        eyeIcon.src = "../images/eye2.png"; // Şifre görünürse eye2.png'ye geç
      } else {
        passwordInput.type = "password";
        eyeIcon.src = "../images/eye.png"; // Şifre gizliyse eye.png'ye geri dön
      }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const rememberCheckbox = document.getElementById("remember");
    const emailInput = document.getElementById("username");
  
    // Daha önce seçilmişse checkbox'ı işaretle ve email'i inputa yaz
    if (localStorage.getItem("rememberMe") === "true") {
      rememberCheckbox.checked = true;
      emailInput.value = localStorage.getItem("savedEmail") || "";
    }
  
    rememberCheckbox.addEventListener("change", function () {
      if (this.checked) {
        localStorage.setItem("rememberMe", "true");
        localStorage.setItem("savedEmail", emailInput.value);
        
      } else {
        localStorage.setItem("rememberMe", "false");
        localStorage.removeItem("savedEmail");
        
      }
    });
  
    // Kullanıcı e-posta girerken her değişiklikte sakla
    emailInput.addEventListener("input", function () {
      if (rememberCheckbox.checked) {
        localStorage.setItem("savedEmail", emailInput.value);
      }
    });
});


document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.querySelector("form");

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault(); // Sayfanın yeniden yüklenmesini engeller

        // Önce inputları al
        const emailInput = document.getElementById("username");
        const passwordInput = document.getElementById("password");

        // Hata mesajlarını göstermek için alanları al
        const emailError = document.getElementById("emailError");
        const passwordError = document.getElementById("passwordError");

        // Önceki hata mesajlarını temizle
        emailError.innerText = "";
        passwordError.innerText = "";

        let hasError = false;

        // Email doğrulaması
        const email = emailInput.value.trim();
        if (!email) {
            emailError.innerText = "* E-posta alanı boş bırakılamaz.";
            hasError = true;
        } else if (!/^\S+@\S+\.\S+$/.test(email)) {
            emailError.innerText = "* Geçerli bir e-posta adresi giriniz.";
            hasError = true;
        }

        // Şifre doğrulaması
        const password = passwordInput.value.trim();
        if (!password) {
            passwordError.innerText = "* Şifre alanı boş bırakılamaz.";
            hasError = true;
        } else if (password.length < 6) {
            passwordError.innerText = "* Şifre en az 10 karakter uzunluğunda olmalıdır.";
            hasError = true;
        }

        // Eğer hata varsa, backend'e istek atma
        if (hasError) return;

        // Backend'e giriş isteği gönder
        const loginData = {
            email: email,
            password: password
        };

        try {
            const response = await fetch("https://localhost:8443/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(loginData),
                credentials: "include" // Çerezleri dahil et
               
            });

            const result = await response.json();

            if (!response.ok || !result.result) {
                console.error("Giriş başarısız:", result);

                // Eğer backend validasyon hataları varsa, ilgili alanlara yaz
                if (result.data && typeof result.data === "object") {
                    if (result.data.email) emailError.innerText = result.data.email.join(", ");
                    if (result.data.password) passwordError.innerText = result.data.password.join(", ");
                } else {
                    showToastMessage(result.errorMessage || "Giriş başarısız!", false);
                }
                return;
            }

            // API yanıtında data objesi içinde kullanıcı bilgileri var
            const userData = result.data;  

            if (userData) {
            
                // Kullanıcıyı ilgili sayfaya yönlendir
                window.location.href = userData.redirectUrl;
            } else {
                showToastMessage("Sunucudan geçersiz yanıt alındı!", false);
            }


        } catch (error) {
            console.error("Giriş sırasında hata oluştu:", error.message);
            showToastMessage(error.message, false);
        }
    });
});