document.addEventListener("DOMContentLoaded", function () {
    
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');
    const subTabButtons = document.querySelectorAll('.sub-tab-button');
    const subTabContents = document.querySelectorAll('.sub-tab-content');
    const likesCommentsTab = document.getElementById('likes-comments');

   
    const defaultTab = document.querySelector('.tab-button[data-tab="posts"]');
    const defaultTabContent = document.getElementById('posts');
    defaultTab.classList.add('active');
    defaultTabContent.classList.add('active');

   
    tabButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Aktif sınıfları temizle
            tabButtons.forEach(button => button.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            subTabButtons.forEach(button => button.classList.remove('active'));
            subTabContents.forEach(content => content.classList.remove('active'));

            // Tıklanan sekmeyi aktif yap
            button.classList.add('active');
            const tab = button.getAttribute('data-tab');
            const activeTabContent = document.getElementById(tab);
            activeTabContent.classList.add('active');

            if (tab === 'likes-comments') {
                console.log('Hareketler sekmesi aktif');
                
                likesCommentsTab.querySelector('.sub-tab-buttons').style.display = 'flex';

                
                subTabButtons.forEach(button => {
                    if (button.getAttribute('data-subtab') === 'likes') {
                        button.classList.add('active');
                    } else {
                        button.classList.remove('active');
                    }
                });

                subTabContents.forEach(content => {
                    if (content.id === 'likes') {
                        content.classList.add('active');
                    } else {
                        content.classList.remove('active');
                    }
                });

            } else {
                console.log('Diğer sekme aktif');
               
                likesCommentsTab.querySelector('.sub-tab-buttons').style.display = 'none';
            }
        });
    });

    
    subTabButtons.forEach(button => {
        button.addEventListener('click', function () {
            
            subTabButtons.forEach(button => button.classList.remove('active'));
            subTabContents.forEach(content => content.classList.remove('active'));

            
            button.classList.add('active');
            const subTab = button.getAttribute('data-subtab');
            const activeSubTabContent = document.getElementById(subTab);
            activeSubTabContent.classList.add('active');
        });
    });
});
