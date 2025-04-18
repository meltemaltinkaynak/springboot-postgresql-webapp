// Toast bildirimi fonksiyonu
function showToastMessage(message, isSuccess = true) {
    const toast = document.getElementById("toast");

    // Eğer mesaj bir array ise, her mesajı alt alta yazdır
    if (Array.isArray(message)) {
        toast.innerHTML = message.map(msg => `<div>${msg}</div>`).join("");
    } else {
        toast.innerText = message;
    }

    toast.style.backgroundColor = isSuccess ? "#28a745" : "#dc3545"; // Yeşil başarı, kırmızı hata
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 8000); // 8 saniye sonra kaybolacak
}

document.addEventListener("DOMContentLoaded", async function () {

    let userId;  // Burada userId'yi global değişken olarak tanımlıyoruz

    // Kullanıcı bilgilerini al
    async function currentUser() {
        try {
            const response = await fetch("https://localhost:8443/auth/me", {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) throw new Error("Kullanıcı bilgisi alınırken hata oluştu.");

            const jsonResponse = await response.json();
            userId = jsonResponse.data.userId;  // User ID'yi al

            // Kullanıcı profilini al
            const userProfile = await fetchUserProfile(userId);  // Profil bilgilerini al
            displayUserProfile(userProfile);  // Profil bilgilerini ekrana yazdır

        } catch (error) {
            console.error("Mevcut kullanıcı hatası:", error);
        }
    }

    // Kullanıcı profilini al
    async function fetchUserProfile(userId) {
        try {
            const response = await fetch(`https://localhost:8443/users/${userId}`, {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) throw new Error("Profil bilgileri alınırken hata oluştu.");

            const jsonResponse = await response.json();
            return jsonResponse.data;  // Profil verisini döndür

        } catch (error) {
            console.error("Yazar yükleme hatası:", error);
            return null;  // Hata durumunda null döndür
        }
    }

    // Kullanıcı profilini ekranda göster
    function displayUserProfile(userProfile) {
        if (userProfile) {
            // Profil fotoğrafı ve diğer bilgileri güncelle
            //const profilePhoto = userProfile.profilePhoto || "images/defaultpp.jpg";
            // Profil fotoğrafı URL'si Spring Boot'dan alınan dosyanın URL'sine uygun olmalı
            const photoUrl = `https://localhost:8443${userProfile.profilePhoto}` || "images/defaultpp.jpg";
            document.getElementById("profile-photo").src = photoUrl;

            // Profil fotoğrafı, backend'den tam URL olarak geldiği için direkt kullanabiliriz
            //const profilePhoto = userProfile.profilePhoto || "images/defaultpp.jpg";  
            //document.getElementById("profile-photo").src = profilePhoto;

            
            document.getElementById("user-role").textContent = userProfile.role;  // Kullanıcı rolünü göster
            document.getElementById("name").textContent = userProfile.firstName;  // Adı göster
            document.getElementById("surname").textContent = userProfile.lastName;  // Soyadı göster
            document.getElementById("email").textContent = userProfile.email;  // E-mail göster
            // Hesap oluşturulma zamanı (ISO formatını gün-ay-yıl şeklinde dönüştür)
            const createdAt = new Date(userProfile.createdAt);
            const day = String(createdAt.getDate()).padStart(2, '0');
            const month = String(createdAt.getMonth() + 1).padStart(2, '0');  // Ayı 1-12 arasında al
            const year = createdAt.getFullYear();
            
            document.getElementById("created-at").textContent = `${day}-${month}-${year}`;  // Gün-Ay-Yıl formatında göster
        } else {
            console.log("Kullanıcı profili alınamadı.");
        }
    }

    // Düzenle butonuna tıklanınca düzenleme alanlarını göster
    document.getElementById("edit-btn").addEventListener("click", function () {
        // Düzenleme alanlarını ve etiketleri göster
        document.getElementById("edit-first-name-container").style.display = "block";
        document.getElementById("edit-last-name-container").style.display = "block";
        document.getElementById("edit-profile-photo-container").style.display = "block";

        // Mevcut profil bilgilerini ve etiketlerini gizle
        document.getElementById("name-container").style.display = "none";
        document.getElementById("surname-container").style.display = "none";
        document.getElementById("email-container").style.display = "none";
        document.getElementById("created-at-container").style.display = "none";
        document.getElementById("user-role-container").style.display = "none";

        // Butonları kontrol et
        document.getElementById("edit-btn").style.display = "none";
        document.getElementById("save-btn").style.display = "block";
    });

    // Güncelleme
    document.getElementById("save-btn").addEventListener("click", async function () {
        const firstName = document.getElementById("edit-first-name").value;
        const lastName = document.getElementById("edit-last-name").value;
        const profilePhoto = document.getElementById("edit-profile-photo").files[0];

        const formData = {};

        // Adı kontrol et
        if (firstName) {
            formData.firstName = firstName;
        }

        // Soyadı kontrol et
        if (lastName) {
            formData.lastName = lastName;
        }

        // Fotoğraf kontrol et
        if (profilePhoto) {
            const reader = new FileReader();
            reader.onloadend = async function () {
                const base64Image = reader.result;
                formData.profilePhoto = base64Image;  // Base64 string'i gönder
                await updateUserProfile(formData);  // Kullanıcı profili güncelle
            };
            reader.readAsDataURL(profilePhoto);  // Fotoğrafı Base64 formatına çevir
        } else if (Object.keys(formData).length > 0) {
            // Fotoğraf yoksa ama ad veya soyad varsa, bu durumda sadece ad veya soyad güncellenmeli
            await updateUserProfile(formData);
        }
    });

    // Kullanıcı bilgilerini güncelleyen fonksiyon
    async function updateUserProfile(formData) {
        try {
            const response = await fetch(`https://localhost:8443/users/${userId}`, {
                method: 'PATCH',
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: 'include',
                body: JSON.stringify(formData)  // Sadece değişen alanlar gönderiliyor
            });

            if (!response.ok) throw new Error("Bilgiler güncellenirken hata oluştu.");
            
            // Başarılı olursa kullanıcıyı güncelle
            showToastMessage("Profil Bilgileriniz başarıyla güncellendi.", true);
            // 8 saniye sonra profile.html sayfasına yönlendir
            setTimeout(() => {
                window.location.href = "profile.html";  // Yönlendirme işlemi
            }, 3000); // Toast mesajının kaybolma süresiyle aynı süreyi ayarladık

            await currentUser();  // Güncel kullanıcı bilgilerini al
        } catch (error) {
            console.error("Güncelleme hatası:", error);
        }
    }

    // İlk olarak mevcut kullanıcıyı al
    currentUser();
});