document.addEventListener("DOMContentLoaded", async function () {
    const contentContainer = document.querySelector(".content-container");

    // Giriş yapmış kullanıcıyı kontrol et
    async function checkLoginStatus() {
        try {
            const authResponse = await fetch("https://localhost:8443/auth/me", {
                method: "GET",
                credentials: "include"
            });
            const authResult = await authResponse.json();
            return authResult.result === true; // true ise giriş yapmış
        } catch (error) {
            console.error("Giriş kontrolü sırasında hata oluştu:", error);
            return false; // hata durumunda giriş yapılmamış kabul edelim
        }
    }

    // İçerikleri getir (kullanıcı giriş durumuna göre farklı endpointler)
    async function fetchContents() {
        const isLoggedIn = await checkLoginStatus();

        try {
            const url = isLoggedIn 
                ? "https://localhost:8443/contents/recent-four/private" 
                : "https://localhost:8443/contents/recent-four";

            const response = await fetch(url, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) throw new Error("İçerikler yüklenirken hata oluştu");

            const jsonResponse = await response.json();
            const contents = jsonResponse.data;

            displayContents(contents);
        } catch (error) {
            console.error("İçerik yükleme hatası:", error);
        }
    }

    // İçerikleri ekrana yazdır
    async function displayContents(contents) {
        contents.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // En yeni yukarıda

        for (const content of contents) {
            const author = await fetchAuthor(content.userId);

            const contentCard = document.createElement("div");
            contentCard.classList.add("content-card");
            contentCard.dataset.contentId = content.contentId;

            const baseUrl = "https://localhost:8443";
            const imageUrl = content.photo ? `${baseUrl}${content.photo}` : "images/default.jpg";

            // restricted kontrolü
            const restrictedLabel = content.restricted ? " | Sadece üyelere özel" : "";

            contentCard.innerHTML = 
                `<img src="${imageUrl}" alt="İçerik Görseli" class="content-image">
                <h2 class="content-title"><a href="content-detail.html?postId=${content.contentId}">${content.title}</a></h2>
                <p class="content-text">${content.text}</p>
                <!-- Meta info: Tarih || Yazar | Kısıtlı içerik -->
                <p class="content-meta">
                      ${new Date(content.createdAt).toLocaleDateString()} | ${content.category} ${restrictedLabel}
                    <span class="content-author">${author}</span>
                </p>`;

            contentContainer.appendChild(contentCard);
        }
    }

    // İçerik yazarını getir
    async function fetchAuthor(userId) {
        try {
            const response = await fetch(`https://localhost:8443/users/public/author/${userId}`, {
                method: 'GET',
                credentials: 'include'
            });

            const jsonResponse = await response.json();
            return jsonResponse.data
                ? `${jsonResponse.data.firstName} ${jsonResponse.data.lastName}`
                : "Yazar Bulunamadı";
        } catch (error) {
            console.error("Yazar yükleme hatası:", error);
            return "Bilinmiyor";
        }
    }

  
    fetchContents();
});
