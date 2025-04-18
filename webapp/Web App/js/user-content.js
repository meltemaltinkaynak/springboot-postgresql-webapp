document.addEventListener("DOMContentLoaded", async function () {
    try {
        // Kullanıcı bilgilerini al
        const response = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include", // Çerezleri göndermek için gerekli
        });

        if (!response.ok) {
            throw new Error(`HTTP hata! Durum: ${response.status}`);
        }

        const data = await response.json();
        
        if (data && data.result && data.data) {
            const user = data.data;

            // İçerik listesi gösterilecek alanı bul
            const contentContainer = document.querySelector("#posts");

            // İçeriklerin yüklendiği anda #posts konteynerini görünür yap
            async function fetchUserContents() {
                try {
                    const response = await fetch("https://localhost:8443/contents/userContents", {
                        method: 'GET',
                        credentials: 'include'
                    });
                    if (!response.ok) throw new Error("İçerikler yüklenirken hata oluştu");

                    const jsonResponse = await response.json();
                    const contents = jsonResponse.data;

                    displayContents(contents);
                } catch (error) {
                    console.error("İçerik yükleme hatası:", error);
                }
            }

            // İçerikleri sayfaya ekle
            async function displayContents(contents) {
                contents.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // Tarihe göre sırala (en yeni en üstte)

                // Her içerik için dinamik post card oluştur
                for (const content of contents) {
                    const author = await fetchAuthor(content.userId);  // İçeriğin yazarını al
                    const commentsCount = await fetchCommentCount(content.contentId);
                    const likesCount = await fetchLikeCount(content.contentId);
        
                    const postCard = document.createElement("div");
                    postCard.classList.add("post-card");
        
                    postCard.innerHTML = `
                        <div class="post-header">
                            <h3 class="post-title"><a href="content-detail.html?postId=${content.contentId}">${content.title}</a></h3>
                            <span class="post-category">${content.category}</span>
                        </div>
                        <div class="post-meta">
                            <span class="post-author">${author}</span>
                            <span class="post-date">${new Date(content.createdAt).toLocaleDateString()}</span>
                            
                        </div>
                        <div class="post-stats">
                            <span class="post-likes">${likesCount} Beğeni</span>
                            <span class="post-comments"> ${commentsCount} Yorum</span>
                        </div>
                    `;

                    contentContainer.appendChild(postCard);
                }
            }

            // Yazar'ı al (içerik yazarı)
            async function fetchAuthor(userId) {
                try {
                    const response = await fetch(`https://localhost:8443/users/author/${userId}`, {
                        method: 'GET',
                        credentials: 'include'
                    });
                    const jsonResponse = await response.json();
                    return jsonResponse.data ? `${jsonResponse.data.firstName} ${jsonResponse.data.lastName}` : "Yazar Bulunamadı";
                } catch (error) {
                    console.error("Yazar yükleme hatası:", error);
                    return "Bilinmiyor";  // Hata durumunda yazar adı "Bilinmiyor" olarak gösterilsin
                }
            }

            // Yorum sayısını al
            async function fetchCommentCount(contentId) {
                try {
                    const response = await fetch(`https://localhost:8443/comments/${contentId}/comment-count`, {
                        method: 'GET',
                        credentials: 'include'
                    });
                    if (!response.ok) {
                        console.error("Yorum sayısı yüklenirken hata oluştu:", response.statusText);
                        return 0;
                    }
                    const jsonResponse = await response.json();
                    return jsonResponse.data || 0;
                } catch (error) {
                    console.error("Yorum sayısı yüklenirken hata oluştu:", error);
                    return 0;
                }
            }

            // Beğeni sayısını al
            async function fetchLikeCount(contentId) {
                try {
                    const response = await fetch(`https://localhost:8443/like/${contentId}/like-count`, {
                        method: 'GET',
                        credentials: 'include'
                    });
                    if (!response.ok) {
                        console.error("Beğeni sayısı yüklenirken hata oluştu:", response.statusText);
                        return 0;
                    }
                    const jsonResponse = await response.json();
                    return jsonResponse.data || 0;
                } catch (error) {
                    console.error("Beğeni sayısı yüklenirken hata oluştu:", error);
                    return 0;
                }
            }

            // Son bir haftanın içeriklerini al
            fetchUserContents();
        }

    } catch (error) {
        console.error("Kullanıcı bilgileri alınırken hata oluştu:", error);
    }
});
