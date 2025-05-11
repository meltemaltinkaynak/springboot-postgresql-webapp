document.addEventListener("DOMContentLoaded", async function () {
    try {
        const response = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include",
        });

        if (!response.ok) throw new Error(`HTTP hata! Durum: ${response.status}`);

        const data = await response.json();

        if (data && data.result && data.data) {
            const user = data.data;
            const contentContainer = document.querySelector("#posts");

            // İçerikleri çek
            const contents = await fetchUserContents();
            if (contents) {
                // Tarihe göre sırala (en yeni en başta)
                contents.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

                // Rank sırasını ters çevir
                let rank = contents.length;
                for (const content of contents) {
                    const contentCard = await createContentCard(content, rank--);
                    contentContainer.appendChild(contentCard);
                }
            }
        }

    } catch (error) {
        console.error("Kullanıcı bilgileri alınırken hata oluştu:", error);
    }
});

async function fetchUserContents() {
    try {
        const response = await fetch("https://localhost:8443/contents/userContents", {
            method: 'GET',
            credentials: 'include'
        });
        if (!response.ok) throw new Error("İçerikler yüklenirken hata oluştu");
        const jsonResponse = await response.json();
        return jsonResponse.data;
    } catch (error) {
        console.error("İçerik yükleme hatası:", error);
        return null;
    }
}

async function createContentCard(content, rank) {
    const author = await fetchAuthor(content.userId);
    const commentsCount = await fetchCommentCount(content.contentId);
    const likesCount = await fetchLikeCount(content.contentId);

    const contentCard = document.createElement("div");
    contentCard.classList.add("content-card");
    contentCard.dataset.contentId = content.contentId;

    const baseUrl = "https://localhost:8443";
    const imageUrl = content.photo ? `${baseUrl}${content.photo}` : "images/default.jpg";

    const restrictedLabel = content.restricted
        ? `<span class="restricted-label">| Sadece üyelere özel</span>`
        : `<span class="restricted-label">| Herkes için erişilebilir</span>`;

    contentCard.innerHTML = `
        <img src="${imageUrl}" alt="İçerik Görseli" class="content-image">
        <h2 class="content-title">
          <div class="title-row">
            <span class="rank-label">#${rank}</span>
            <a href="content-detail.html?postId=${content.contentId}">${content.title}</a>
            <span class="category-label">${content.category}</span>
          </div>
          ${restrictedLabel}
          <span class="meta-info">
              <img src="/images/likes.png" alt="Beğeni" class="icon"> Beğeni: ${likesCount}
          </span>
          <span class="meta-info">
              <img src="/images/comments.png" alt="Yorum" class="icon"> Yorum: ${commentsCount}
          </span>
          <span class="meta-info">
              <img src="/images/views2.png" alt="Görüntülenme" class="icon"> Görüntülenme: ${content.viewCount || 0}
          </span>
          <span class="meta-info">
              <img src="/images/calendar.png" alt="Tarih" class="icon"> Oluşturulma Tarihi: ${new Date(content.createdAt).toLocaleDateString()}
          </span>
        </h2>
    `;

    return contentCard;
}
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
        return "Bilinmiyor";
    }
}

async function fetchCommentCount(contentId) {
    try {
        const response = await fetch(`https://localhost:8443/comments/${contentId}/comment-count`, {
            method: 'GET',
            credentials: 'include'
        });
        const jsonResponse = await response.json();
        return jsonResponse.data || 0;
    } catch (error) {
        console.error("Yorum sayısı yüklenirken hata oluştu:", error);
        return 0;
    }
}

async function fetchLikeCount(contentId) {
    try {
        const response = await fetch(`https://localhost:8443/like/${contentId}/like-count`, {
            method: 'GET',
            credentials: 'include'
        });
        const jsonResponse = await response.json();
        return jsonResponse.data || 0;
    } catch (error) {
        console.error("Beğeni sayısı yüklenirken hata oluştu:", error);
        return 0;
    }
}
