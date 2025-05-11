document.addEventListener("DOMContentLoaded", function () {
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
            userId = jsonResponse.data.userId;  // User ID'yi al
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

    // Kullanıcı profilini modalda göster
    function displayUserProfile(userProfile) {
        if (userProfile) {
           

            // Profil fotoğrafı
            const photoUrl = `https://localhost:8443${userProfile.profilePhoto}` || "images/defaultpp.jpg";
            const profilePhotoElement = document.getElementById("profile-photo");
            if (profilePhotoElement) {
                profilePhotoElement.src = photoUrl;
            }

            // Kullanıcı bilgilerini modalda göster
            const firstNameElement = document.getElementById("edit-first-name");
            const lastNameElement = document.getElementById("edit-last-name");
            const profilePhotoNameElement = document.getElementById("edit-profile-photo-name");

            if (firstNameElement) {
                firstNameElement.value = userProfile.firstName;
            }
            if (lastNameElement) {
                lastNameElement.value = userProfile.lastName;
            }

            // Fotoğraf için mevcut ismi göster
            const profilePhotoName = userProfile.profilePhoto ? userProfile.profilePhoto.split('/').pop() : "Yok";
            if (profilePhotoNameElement) {
                profilePhotoNameElement.textContent = profilePhotoName;
            }

            // E-mail bilgisi
            const emailElement = document.getElementById("email");
            if (emailElement) {
                emailElement.textContent = userProfile.email;
            }

            // Hesap oluşturulma tarihi
            const createdAtElement = document.getElementById("created-at");
            if (createdAtElement) {
                createdAtElement.textContent = new Date(userProfile.createdAt).toLocaleString();
            }

            // Kullanıcı rolü
            const userRoleElement = document.getElementById("user-role");
            if (userRoleElement) {
                userRoleElement.textContent = userProfile.role;
            }
        } else {
            console.log("Kullanıcı profili alınamadı.");
        }
    }

    // account-info sekmesi aktif olduğunda kullanıcı profilini yükle
    async function loadProfileIfActive() {
        const accountInfoTab = document.querySelector('[data-subtab="account-info"]');
        const accountInfoContent = document.getElementById('account-info');

        // Eğer account-info sekmesi aktifse
        if (accountInfoTab && accountInfoTab.classList.contains('active')) {
            await currentUser();  // Önce kullanıcıyı al
            if (userId) {
                const userProfile = await fetchUserProfile(userId);
                displayUserProfile(userProfile);  // Profil bilgilerini modalda göster
            }
        }
    }

    // Sekme tıklamalarını dinle
    document.querySelectorAll('.sub-tab-button').forEach(button => {
        button.addEventListener('click', () => {
            // Aktif sekmeyi kontrol et
            if (button.dataset.subtab === 'account-info') {
                loadProfileIfActive();  // account-info aktifse verileri çek
            }
        });
    });

    

    // Sayfa yüklendiğinde account-info sekmesi aktifse verileri yükle
    loadProfileIfActive();


    
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
            return false;
        
        } else {
            return true;
        }
    }

    // Soyad doğrulama
    function validateLastName(lastName) {
        if (!lastName || lastName.trim() === "") {
            showToastMessage("Soyad alanı boş bırakılamaz.", false); // Hata mesajı toast
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
            showToastMessage("Profil bilgileriniz başarıyla güncellendi.", true);

            await currentUser();  // Güncel kullanıcı bilgilerini al
        } catch (error) {
            console.error("Güncelleme hatası:", error);
            showToastMessage("Bilgiler güncellenirken bir hata oluştu.", false);
        }
    }

    
    currentUser();



});
