// Sayfa yüklendiğinde beğeni durumlarını kontrol et
document.addEventListener("DOMContentLoaded", function () {
    checkUserLikeStatuses();
    observeContentChanges(); // Dinamik içerik değişikliklerini izle
});

// Kullanıcının beğeni durumlarını kontrol eden fonksiyon
async function checkUserLikeStatuses() {
    const contentCards = document.querySelectorAll(".content-card");

    console.log("Bulunan içerik kartları:", contentCards.length);

    for (const card of contentCards) {
        const contentId = card.dataset.contentId;
        const likeButton = card.querySelector(".like-btn");

        if (!likeButton) {
            console.warn(`İçerik kartında beğeni butonu bulunamadı! İçerik ID: ${contentId}`);
            continue;
        }

        try {
            const response = await fetch(`https://localhost:8443/like/${contentId}/status`, {
                method: "GET",
                credentials: "include"
            });

            if (response.ok) {
                const jsonResponse = await response.json();
                if (jsonResponse.data) {
                    likeButton.classList.add("liked");
                } else {
                    likeButton.classList.remove("liked");
                }
            } else {
                console.error(`Beğeni durumu kontrol edilirken API hatası oluştu (ID: ${contentId})`);
            }
        } catch (error) {
            console.error(`Beğeni durumu kontrol edilirken hata oluştu (ID: ${contentId}):`, error);
        }
    }
}

// Beğeni butonuna tıklandığında 
document.body.addEventListener("click", async function (event) {
    if (event.target.closest(".like-btn")) {
        const likeButton = event.target.closest(".like-btn");
        console.log("Beğeni butonuna tıklandı:", likeButton);

        const contentCard = likeButton.closest(".content-card");
        const contentId = contentCard.dataset.contentId;
        const likeCountElement = likeButton.querySelector(".like-count");

        try {
            const response = await fetch(`https://localhost:8443/like/${contentId}`, {
                method: "PUT",
                credentials: "include"
            });

            const jsonResponse = await response.json();

            if (!response.ok) {
               
                const errorMessage = jsonResponse.data?.description || jsonResponse.errorMessage || "Beğeni işlemi başarısız!";
                showToastMessage(errorMessage, false);
                return;
            }

            // Beğeni durumu değiştiyse butonun rengini güncelle
            const message = jsonResponse.data.message;
            if (message === "Beğeni eklendi.") {
                likeButton.classList.add("liked");
            } else {
                likeButton.classList.remove("liked");
            }

            // Beğeni sayısını güncelle
            const newLikeCount = await fetchLikeCount(contentId);
            likeCountElement.textContent = newLikeCount;

        } catch (error) {
            console.error("Beğeni işlemi sırasında hata oluştu:", error);
            showToastMessage("Bir hata meydana geldi, lütfen tekrar deneyin!", false);
        }
    }
});

// Beğeni sayısı
async function fetchLikeCount(contentId) {
    try {
        const response = await fetch(`https://localhost:8443/like/${contentId}/like-count`, {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) return 0;

        const jsonResponse = await response.json();
        return jsonResponse.data || 0;
    } catch (error) {
        console.error("Beğeni sayısı yüklenirken hata oluştu:", error);
        return 0;
    }
}

// Dinamik olarak yeni içerikler eklendiğinde beğeni durumlarını tekrar kontrol 
function observeContentChanges() {
    const observer = new MutationObserver(() => {
       
        checkUserLikeStatuses();
    });

    observer.observe(document.body, { childList: true, subtree: true });
}

