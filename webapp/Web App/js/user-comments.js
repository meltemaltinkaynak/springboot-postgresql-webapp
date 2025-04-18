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

            // Yorumlar listesi gösterilecek alanı bul
            const commentContainer = document.querySelector("#comments");

            // Yorumları yükle
            async function fetchUserComments() {
                try {
                    const response = await fetch(`https://localhost:8443/comments/comment-list`, {
                        method: 'GET',
                        credentials: 'include'
                    });
                    if (!response.ok) throw new Error("Yorumlar yüklenirken hata oluştu");

                    const jsonResponse = await response.json();
                    const comments = jsonResponse.data;

                    displayComments(comments);
                } catch (error) {
                    console.error("Yorum yükleme hatası:", error);
                }
            }

            // Yorumları sayfaya ekle
            async function displayComments(comments) {
                if (comments.length === 0) {
                    const noCommentsMessage = document.createElement("p");
                    noCommentsMessage.innerText = "Henüz yorum yapmadınız.";
                    commentContainer.appendChild(noCommentsMessage);
                } else {
                    comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // Tarihe göre sırala (en yeni en üstte)

                    // Her yorum için dinamik comment card oluştur
                    for (const comment of comments) {
                        const commentCard = document.createElement("div");
                        commentCard.classList.add("comment-card");

                        commentCard.innerHTML = `
                            <div class="comment-header">
                                <h3 class="comment-title">
                                    <a href="content-detail.html?postId=${comment.contentId}">
                                        "${comment.contentTitle}" <span class="comment-description">adlı içeriğe yorum yaptınız.</span>
                                    </a>
                                </h3>
                                <div class="comment-meta">
                                    <span class="comment-date">${new Date(comment.createdAt).toLocaleDateString()}</span>
                                </div>
                            </div>
                            <div class="comment-text">"${comment.commentText}"</div>
                        `;

                        commentContainer.appendChild(commentCard);
                    }
                }
            }

            // Yorumları al
            fetchUserComments();
        }

    } catch (error) {
        console.error("Kullanıcı bilgileri alınırken hata oluştu:", error);
    }
});
