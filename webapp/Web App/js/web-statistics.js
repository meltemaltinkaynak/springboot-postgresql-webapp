document.addEventListener("DOMContentLoaded", async function () {
    const contentCountSpan = document.getElementById("contentCount");
    const authorCountSpan = document.getElementById("authorCount");
    const viewCountSpan = document.getElementById("viewCount");

    async function fetchStatistics() {
        try {
            const response = await fetch("https://localhost:8443/api/statistics", {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("İstatistikler yüklenirken hata oluştu");
            }

            const jsonResponse = await response.json();

            if (jsonResponse.result) {
                const statistics = jsonResponse.data;
                displayStatistics(statistics);
            } else {
                console.error("İstatistik verisi alınamadı:", jsonResponse.errorMessage);
            }
        } catch (error) {
            console.error("İstatistik yükleme hatası:", error);
        }
    }

    function displayStatistics(statistics) {
        contentCountSpan.textContent = statistics.totalContentCount+"+";
        authorCountSpan.textContent = statistics.totalAuthorCount+"+";
        viewCountSpan.textContent = statistics.totalViewCount+"+";
    }

   
    fetchStatistics();
});


