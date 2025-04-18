document.addEventListener("DOMContentLoaded", function () {
    // Sekmelerin butonlarını seç
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');
    const subTabButtons = document.querySelectorAll('.sub-tab-button');
    const subTabContents = document.querySelectorAll('.sub-tab-content');
    const likesCommentsTab = document.getElementById('likes-comments');

    // Sayfa yüklendiğinde "İçerikler" sekmesini aktif yap
    const defaultTab = document.querySelector('.tab-button[data-tab="posts"]');
    const defaultTabContent = document.getElementById('posts');
    defaultTab.classList.add('active');
    defaultTabContent.classList.add('active');

    // Hareketler sekmesi tıklama olayını dinle
    tabButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Aktif butonları kaldır
            tabButtons.forEach(button => button.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            subTabButtons.forEach(button => button.classList.remove('active'));
            subTabContents.forEach(content => content.classList.remove('active'));

            // Tıklanan butonu aktif yap
            button.classList.add('active');

            // Aktif içeriği göster
            const tab = button.getAttribute('data-tab');
            const activeTabContent = document.getElementById(tab);
            activeTabContent.classList.add('active');

            // Eğer Hareketler sekmesi aktifse, alt sekme butonlarını göster
            if (tab === 'likes-comments') {
                console.log('Hareketler sekmesi aktif');
                likesCommentsTab.querySelector('.sub-tab-buttons').style.display = 'flex'; // Alt sekme butonlarını göster
            } else {
                console.log('Diğer sekme aktif');
                likesCommentsTab.querySelector('.sub-tab-buttons').style.display = 'none'; // Alt sekme butonlarını gizle
            }
        });
    });

    // Alt sekme butonları için tıklama olayını dinle
    subTabButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Aktif alt sekme butonlarını kaldır
            subTabButtons.forEach(button => button.classList.remove('active'));
            subTabContents.forEach(content => content.classList.remove('active'));

            // Tıklanan alt sekme butonunu aktif yap
            button.classList.add('active');

            // Aktif alt sekme içeriğini göster
            const subTab = button.getAttribute('data-subtab');
            const activeSubTabContent = document.getElementById(subTab);
            activeSubTabContent.classList.add('active');
        });
    });
});
