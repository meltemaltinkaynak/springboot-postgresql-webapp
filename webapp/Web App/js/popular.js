document.addEventListener("DOMContentLoaded", function () {
    init();
});

async function init() {
    let isLoggedIn = false;

    //Giriş kontrolü 
    try {
        const authResponse = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include"
        });
        const authResult = await authResponse.json();
        isLoggedIn = authResult.result === true;
    } catch (error) {
        console.error("Giriş kontrolü sırasında hata oluştu:", error);
    }

    // auth/me cevabına göre doğru endpointi belirle
    const tabConfigs = {
        top10: {
            endpoint: isLoggedIn ? "https://localhost:8443/contents/top10/private" : "https://localhost:8443/contents/top10",
            show: ["viewCount", "likeCount", "commentCount"],
            showTotal: true
        },
        likes: {
            endpoint: isLoggedIn ? "https://localhost:8443/contents/top10/likes/private" : "https://localhost:8443/contents/top10/likes",
            show: ["likeCount"],
            showTotal: false
        },
        comments: {
            endpoint: isLoggedIn ? "https://localhost:8443/contents/top10/comments/private" : "https://localhost:8443/contents/top10/comments",
            show: ["commentCount"],
            showTotal: false
        },
        views: {
            endpoint: isLoggedIn ? "https://localhost:8443/contents/top10/views/private" : "https://localhost:8443/contents/top10/views",
            show: ["viewCount"],
            showTotal: false
        }
    };

    //auth/me tamamlandıktan sonra istek
    for (const [tabId, config] of Object.entries(tabConfigs)) {
        const container = document.querySelector(`#${tabId} .content-container`);
        fetchAndDisplayContents(config.endpoint, container, config.show, config.showTotal);
    }
}

// İçerik çekme ve gösterme
async function fetchAndDisplayContents(endpoint, container, showFields, showTotal) {
    try {
        const response = await fetch(endpoint, {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) throw new Error("Veri alınamadı");

        const json = await response.json();
        const contents = json.data;

        let totalViews = 0, totalLikes = 0, totalComments = 0;

        // İçeriklerin sırasını eklemek için sıralama işlemi 
        contents.forEach((content, index) => {
            content.rank = index + 1; // Her içerik için sıralama numarasını belirliyoruz
        });

        for (const content of contents) {
            const contentCard = await createContentCard(content, showFields);
            container.appendChild(contentCard);

            if (showTotal) {
                totalViews += content.viewCount || 0;
                totalLikes += content.likeCount || 0;
                totalComments += content.commentCount || 0;
            }
        }

        
    } catch (error) {
        console.error(`İçerikler alınamadı: ${endpoint}`, error);
    }
}

// Kart oluşturma 
async function createContentCard(content, showFields) {
    const contentCard = document.createElement("div");
    contentCard.classList.add("content-card");
    contentCard.dataset.contentId = content.contentId;

    const baseUrl = "https://localhost:8443";
    const imageUrl = content.photo ? `${baseUrl}${content.photo}` : "images/default.jpg";

    
    const restrictedLabel = content.restricted 
    ? `<span class="restricted-label">| Sadece üyelere özel</span>` 
    : `<span class="restricted-label">| Herkes için erişilebilir</span>`;

    

    contentCard.innerHTML = `
        <div class="content-rank">${content.rank} </div>
        <img src="${imageUrl}" alt="İçerik Görseli" class="content-image">
        <h2 class="content-title">
            <a href="content-detail.html?postId=${content.contentId}">${content.title}</a>
            
            ${restrictedLabel}
            
            <span class="meta-info">
                <img src="/images/likes.png" alt="Beğeni" class="icon"> Beğeni: ${content.likeCount}
            </span>
            <span class="meta-info">
                <img src="/images/comments.png" alt="Yorum" class="icon"> Yorum: ${content.commentCount}
            </span>
            <span class="meta-info">
              <img src="/images/views2.png" alt="Görüntülenme" class="icon"> Görüntülenme: ${content.viewCount}
            </span>
            

        </h2>
        
    `;

    return contentCard;
}
