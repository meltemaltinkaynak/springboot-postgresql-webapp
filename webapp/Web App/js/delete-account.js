// Modalı aç
document.getElementById("deleteAccountBtn").addEventListener("click", () => {
    document.getElementById("deleteModal").style.display = "block";
  });
  
  // Modalı kapat
  document.getElementById("cancelDeleteBtn").addEventListener("click", () => {
    document.getElementById("deleteModal").style.display = "none";
  });
  
  // Hesabı silme işlemi
  document.getElementById("finalDeleteBtn").addEventListener("click", () => {
    const deleteAlsoContent = document.getElementById("deleteToggle").checked;
  
    fetch("https://localhost:8443/users/delete-account", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include", 
      body: JSON.stringify({ deleteContent: deleteAlsoContent })
    })
      .then(response => response.json())
      .then(data => {
        if (data.result) {
          showToastMessage(data.data, true); // Başarılı işlem
          setTimeout(() => {
            window.location.href = "index.html"; //  anasayfaya yönlendir
          }, 2000);
        } else {
          showToastMessage(data.errorMessage, false); //
        }
      })
      .catch(error => {
        console.error("Hesap silme hatası:", error);
        showToastMessage("Bir hata oluştu. Lütfen tekrar deneyin.", false);
      });
  
    // Modalı kapat
    document.getElementById("deleteModal").style.display = "none";
  });
  