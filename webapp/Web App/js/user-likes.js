document.addEventListener("DOMContentLoaded", async function () {
    try {
        // Kullanıcı bilgilerini al
        const response = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include", 
        });

        if (!response.ok) {
            throw new Error(`HTTP hata! Durum: ${response.status}`);
        }

        const data = await response.json();
        
        if (data && data.result && data.data) {
            const user = data.data;

            // Beğeniler listesi gösterilecek alanı bul
            const likeContainer = document.querySelector("#likes");

            // Beğenileri yükle
            async function fetchUserLikes() {
                try {
                    const response = await fetch(`https://localhost:8443/like/like-list`, {
                        method: 'GET',
                        credentials: 'include'
                    });
                    if (!response.ok) throw new Error("Beğeniler yüklenirken hata oluştu");

                    const jsonResponse = await response.json();
                    const likes = jsonResponse.data;

                    displayLikes(likes);
                } catch (error) {
                    console.error("Beğeni yükleme hatası:", error);
                }
            }

            // Beğenileri sayfaya ekle
            async function displayLikes(likes) {
                likes.sort((a, b) => new Date(b.likeCreatedAt) - new Date(a.likeCreatedAt)); // Tarihe göre sırala (en yeni en üstte)

                // Her beğeni için dinamik like card oluştur
                for (const like of likes) {
                    const likeCard = document.createElement("div");
                    likeCard.classList.add("like-card");

                    likeCard.innerHTML = `
                        <div class="like-header">
                            <h3 class="like-title"><a href="content-detail.html?postId=${like.contentId}">"${like.contentTitle}"
                              <span class="like-description"> adlı içeriği beğendin.</span></a> </h3>
                             <div class="like-meta">
                            <span class="like-date">${new Date(like.likeCreatedAt).toLocaleDateString()}</span>
                        </div>
                        </div>
                       
                    `;

                    likeContainer.appendChild(likeCard);
                }
            }

            // Beğenileri al
            fetchUserLikes();
        }

    } catch (error) {
        console.error("Kullanıcı bilgileri alınırken hata oluştu:", error);
    }
});
