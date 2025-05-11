document.addEventListener("DOMContentLoaded", async function () {
    try {
        
        const category = "last-week"

        if (!category) {
            console.error("Kategori parametresi URL'den alınamadı.");
            return;
        }

        // Kullanıcı bilgilerini al
        const response = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include",
        });

        if (!response.ok) {
            throw new Error(`HTTP hata! Durum: ${response.status}`);
        }

        const data = await response.json();

        // İçerik listesi gösterilecek alan
        const contentContainer = document.querySelector(".content-container");

        // İçerikleri sayfaya ekle
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
                const restrictedLabel = content.restricted ? " | Kısıtlı içerik" : "";

                contentCard.innerHTML = `
                <h2 class="content-title"><a href="content-detail.html?postId=${content.contentId}">${content.title}</a></h2>
                <img src="${imageUrl}" alt="İçerik Görseli" class="content-image">
                
                <p class="content-text">${content.text}</p>
                <!-- Meta info: Tarih || Yazar | Kısıtlı içerik | Kategori -->
                <p class="content-meta">
                    ${new Date(content.createdAt).toLocaleDateString()} | ${content.category}${restrictedLabel} 
                    
                    <span class="content-author">${author}</span>
                </p>
                `;

                contentContainer.appendChild(contentCard);
            }
        }

        // İçerik yazarı
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

        // Giriş yapmışsa tüm içerikleri yükle
        if (data && data.result && data.data) {
            const user = data.data;

            // İçerik yükleme fonksiyonu
            async function fetchContent(category) {
                try {
                    const response = await fetch(`https://localhost:8443/contents/${category}`, {
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

            // Kategoriyi yükle
            await fetchContent(category); 

        } else {
            // Giriş yapmamışsa, herkese kısıtlı içerikleri yükle
            async function fetchPublicContent(category) {
                try {
                    const response = await fetch(`https://localhost:8443/contents/${category}/public`, {
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

            // İçerikleri ekrana yazdır
            await fetchPublicContent(category); 
        }

    } catch (error) {
        console.error("Kullanıcı bilgisi alınamadı:", error);
    }
});
