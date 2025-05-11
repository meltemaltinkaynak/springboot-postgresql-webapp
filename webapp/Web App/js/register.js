document.addEventListener("DOMContentLoaded", function () {   
    const form = document.querySelector("form");  
  
    form.addEventListener("submit", async function (e) {  //Kullanıcı "Kayıt Ol" butonuna bastığında, sayfanın yenilenmesini engelliyoruz.
      e.preventDefault();  
  
      //  Formdan Girilen Veriler
      const firstName = document.getElementById("firstname").value.trim();
      const lastName = document.getElementById("lastname").value.trim();
      const email = document.getElementById("email").value.trim();
      const password = document.getElementById("password").value;
      const confirmPassword = document.getElementById("confirm-password").value;
  
  
  
  
     
      const requestData = { 
        firstName: firstName, 
        lastName: lastName, 
        email: email, 
        password: password, 
        confirmPassword: confirmPassword 
      };
  
      try {
        const response = await fetch("https://localhost:8443/auth/register", { 
          method: "POST", // POST isteği
          headers: {
            "Content-Type": "application/json", 
          },
          body: JSON.stringify(requestData), 
        });
  
        
   
        const result = await response.json();  
  
  
        //API isteği başarısız olduysa
        if (!response.ok) {
          if (result.data) {
            // Eğer API'den detaylı hata mesajları geldiyse, dizi olarak gönder
            const errorMessages = Object.values(result.data).flat(); 
            showToastMessage(errorMessages, false);
          } else {
            // Genel hata mesajı varsa göster yoksa diğerini göster
            showToastMessage(result.errorMessage || "Kayıt sırasında bilinmeyen bir hata oluştu!", false);
          }
          return;
        }
        
  
        //kullanıcı başarıyla kayıt olursa
        if (result.result) {
          const fullName = `${result.data.firstName} ${result.data.lastName}`; // Ad ve Soyadı al
          showToastMessage(`Kayıt başarılı!\nHoş geldin ${fullName}.`); // kayıt başarılıysa  ad soyad döndürür
  
           // 8 saniye sonra giriş sayfasına yönlendir
           setTimeout(() => {
            window.location.href = "login.html";
          }, 8000);
         
        } else {
          showToastMessage("Hata: " + (result.errorMessage || "Bilinmeyen bir hata oluştu."), false); 
        }
      } catch (error) {
        console.error("Kayıt sırasında hata oluştu:", error.message);
        showToastMessage(error.message, false);
      }
    });
  });
