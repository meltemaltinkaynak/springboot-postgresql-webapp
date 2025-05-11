document.addEventListener("DOMContentLoaded", async function () {
    const popularContentContainer = document.querySelector(".popular-content");

    async function fetchMostViewedContents() {
        try {
            const response = await fetch("https://localhost:8443/contents/mostViewedContent", {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) throw new Error("Popüler içerikler yüklenirken hata oluştu");

            const jsonResponse = await response.json();
            const contents = jsonResponse.data;

            displayPopularContents(contents);
        } catch (error) {
            console.error("Popüler içerik yükleme hatası:", error);
        }
    }

    async function displayPopularContents(contents) {
        popularContentContainer.innerHTML = "";
    
        let index = 1;
        for (const content of contents) {
            const author = await fetchAuthor(content.userId);
            const date = new Date(content.createdAt).toLocaleDateString();
    
            const item = document.createElement("div");
            item.classList.add("popular-item");
    
            item.innerHTML = `
                <span class="index-number">${index}</span> 
                <div class="popular-item-content">
                    <a href="content-detail.html?postId=${content.contentId}">${content.title}</a>
                    <div class="popular-item-meta">
                        ${date} | ${author} | ${content.viewCount}  
                       
                </div>
            `;
    
            popularContentContainer.appendChild(item);
            index++;
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

    
    fetchMostViewedContents();
});
