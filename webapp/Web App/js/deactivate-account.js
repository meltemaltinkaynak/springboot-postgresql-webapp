// Modalı aç
document.getElementById("deactivateAccountBtn").addEventListener("click", () => {
    document.getElementById("deactivateModal").style.display = "block";
});

// Modalı kapat
document.getElementById("cancelDeactivateBtn").addEventListener("click", () => {
    document.getElementById("deactivateModal").style.display = "none";
});

// Hesabı devre dışı bırakma işlemi
document.getElementById("finalDeactivateBtn").addEventListener("click", () => {
    
 fetch("https://localhost:8443/auth/disable", {
        method: "PUT",  // PUT isteği
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",  // Çerezleri gönder
    })
        .then(response => response.json())
        .then(data => {
            if (data.result) {
                showToastMessage(data.data, true); // Başarılı işlem
                setTimeout(() => {
                    window.location.href = "index.html"; // Anasayfaya yönlendir
                }, 2000);
            } else {
                showToastMessage(data.errorMessage, false); // Hata mesajı
            }
        })
        .catch(error => {
            console.error("Hesap devre dışı bırakma hatası:", error);
            showToastMessage("Bir hata oluştu. Lütfen tekrar deneyin.", false);
        });

    // Modalı kapat
    document.getElementById("disableModal").style.display = "none";
});