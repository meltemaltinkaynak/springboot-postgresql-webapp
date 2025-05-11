document.addEventListener("DOMContentLoaded", async function () {
    let userId;  

    // Kullanıcı bilgilerini al
    async function currentUser() {
        try {
            const response = await fetch("https://localhost:8443/auth/me", {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) throw new Error("Kullanıcı bilgisi alınırken hata oluştu.");

            const jsonResponse = await response.json();
            userId = jsonResponse.data.userId;  

            // Kullanıcı profilini al
            const userProfile = await fetchUserProfile(userId);  
            displayUserProfile(userProfile); 

            // Profil bilgilerini düzenleme ekranına yazdır
            if (userProfile) {
                populateEditModal(userProfile);  
            }

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
            const photoUrl = `https://localhost:8443${userProfile.profilePhoto}` || "images/defaultpp.jpg";
            document.getElementById("profile-photo").src = photoUrl;
            
            document.getElementById("name").textContent = userProfile.firstName;
            document.getElementById("surname").textContent = userProfile.lastName;
            document.getElementById("email").textContent = userProfile.email;

            const createdAt = new Date(userProfile.createdAt);
            const day = String(createdAt.getDate()).padStart(2, '0');
            const month = String(createdAt.getMonth() + 1).padStart(2, '0');
            const year = createdAt.getFullYear();

            document.getElementById("created-at").textContent = `${day}-${month}-${year}`;
        } else {
            console.log("Kullanıcı profili alınamadı.");
        }
    }

    // Edit Modal'a mevcut ad, soyad ve fotoğraf dosyasını yazdır
    function populateEditModal(userProfile) {
        // Eğer profil fotoğrafı varsa, fotoğrafı göster
        const photoUrl = userProfile.profilePhoto ? `https://localhost:8443${userProfile.profilePhoto}` : "images/defaultpp.jpg";
        document.getElementById("edit-profile-photo").textContent = userProfile.profilePhoto ? userProfile.profilePhoto.split('/').pop() : "Yok";
        
        // Ad ve soyadı alanlarını mevcut değerlerle doldur
        document.getElementById("edit-first-name").value = userProfile.firstName || ""; // Ad
        document.getElementById("edit-last-name").value = userProfile.lastName || ""; // Soyad
    
        // Fotoğrafı modalda göster
        const editProfilePhotoElement = document.getElementById("edit-profile-photo");
        editProfilePhotoElement.src = photoUrl;  // Fotoğrafı modalda da göster
    
        // Profil fotoğrafının ismini ayıklayıp göster
        const photoFileName = userProfile.profilePhoto ? userProfile.profilePhoto.split('/').pop() : "Yok";
        document.getElementById("edit-profile-photo-name").textContent = photoFileName;
    }
    

    // Modal Başlatma: edit-icon'a tıklayınca
    document.getElementById("edit-icon").addEventListener("click", async function () {
        console.log("Edit icon clicked!");  // Log ekledim

        // Kullanıcı bilgilerini güncel al
        const userProfile = await fetchUserProfile(userId);
        if (userProfile) {
            populateEditModal(userProfile);  // Modalda yeni bilgileri göster
        }

        // Modal'ı aç
        document.getElementById("edit-modal").style.display = "block";  
    });

    // Modalı kapatma
    document.getElementById("close-modal").addEventListener("click", function () {
        document.getElementById("edit-modal").style.display = "none"; 
    });

    // Modal dışında bir yere tıklanınca kapat
    window.addEventListener("click", function (event) {
        console.log("Modal dışına tıklanmış mı?", event.target === document.getElementById("edit-modal"));
        if (event.target === document.getElementById("edit-modal")) {
            document.getElementById("edit-modal").style.display = "none"; 
        }
    });

    
    // Profil bilgilerini kaydet
    document.getElementById("save-btn").addEventListener("click", function () {
        const firstName = document.getElementById("edit-first-name").value;
        const lastName = document.getElementById("edit-last-name").value;
        const profilePhoto = document.getElementById("edit-profile-photo").files[0];

        // Validasyon kontrolleri
        const isValidFirstName = validateFirstName(firstName);
        const isValidLastName = validateLastName(lastName);

        // Yalnızca geçerli alanlar için hata mesajı gösterilecek
        if (isValidFirstName && isValidLastName) {
            saveProfileChanges(firstName, lastName, profilePhoto);
        }
    });

    // Ad doğrulama
    function validateFirstName(firstName) {
        if (!firstName || firstName.trim() === "") {
            showToastMessage("Ad alanı boş bırakılamaz.", false); 
        
        } else {
            return true;
        }
    }

    // Soyad doğrulama
    function validateLastName(lastName) {
        if (!lastName || lastName.trim() === "") {
            showToastMessage("Soyad alanı boş bırakılamaz.", false); 
            return false;
        
        } else {
            return true;
        }
    }


    // Profil değişikliklerini kaydet
    async function saveProfileChanges(firstName, lastName, profilePhoto) {
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
    }

    // Kullanıcı bilgilerini güncelle
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
            showToastMessage("Profil bilgileriniz başarıyla güncellendi.", true);

            await currentUser();  // Güncel kullanıcı bilgilerini al
        } catch (error) {
            console.error("Güncelleme hatası:", error);
            showToastMessage("Bilgiler güncellenirken bir hata oluştu.", false);
        }
    }

    
    currentUser();
});
