document.addEventListener("DOMContentLoaded", function () {
    // Navbar'ı yükle
    fetch("navbar.html")
        .then(response => response.text())
        .then(data => {
            document.getElementById("navbar-container").innerHTML = data;
            initNavbarScripts(); // Navbar yüklendikten sonra eventleri bağla
            fetchUserInfo();     // Kullanıcı bilgisini navbar yüklendikten sonra çek
        })
        .catch(error => console.error("Navbar yüklenirken hata oluştu:", error));
});

function fetchUserInfo() {
    fetch("https://localhost:8443/auth/me", {
        method: "GET",
        credentials: "include"
    })
    .then(response => {
        if (!response.ok) throw new Error(`HTTP hata! Durum: ${response.status}`);
        return response.json();
    })
    .then(data => {
        if (data && data.result && data.data) {
            const user = data.data;
            const loginBtn = document.querySelector(".auth-buttons .login-btn");
            const registerBtn = document.querySelector(".auth-buttons .register-btn");
            const userName = document.getElementById("user-name");
            const userInfo = document.getElementById("user-info");
            const accountMenu = document.getElementById("account-menu");

            if (loginBtn) loginBtn.style.display = "none";
            if (registerBtn) registerBtn.style.display = "none";
            if (userName) userName.textContent = `${user.firstName} ${user.lastName}`;
            if (userInfo) userInfo.style.display = "flex";
            if (accountMenu) accountMenu.style.display = "block";

            // Mobil menüdeki giriş/kayıt butonlarını gizle
            const mobileLogin = document.querySelector('#mobile-nav a[href="login.html"]');
            const mobileRegister = document.querySelector('#mobile-nav a[href="register.html"]');
            if (mobileLogin) mobileLogin.parentElement.style.display = "none";
            if (mobileRegister) mobileRegister.parentElement.style.display = "none";
        }
    })
    .catch(error => console.error("Kullanıcı bilgisi alınamadı:", error));
}

function initNavbarScripts() {

    
   

    // Kullanıcı dropdown menüsünü açma ve kapama işlemi
    const dropdown = document.querySelector('.user-dropdown');
    const dropdownMenu = dropdown.querySelector('.dropdown-menu');

    // Dropdown'a tıklanırsa menüyü açma ve kapama
    dropdown.addEventListener('click', function(event) {
        event.stopPropagation(); // Tıklamanın diğer öğelere yayılmasını engeller
        
        if (dropdownMenu.style.display === 'block') {
            dropdownMenu.style.display = 'none';
            dropdownMenu.style.opacity = '0';
            dropdownMenu.style.visibility = 'hidden';
        } else {
            dropdownMenu.style.display = 'block';
            dropdownMenu.style.opacity = '1';
            dropdownMenu.style.visibility = 'visible';
        }

        // Tıklanan menü öğesine 'active' sınıfı ekle
        dropdown.classList.toggle('active');
    });

    // Sayfanın herhangi bir yerine tıklanırsa menüyü kapat
    document.addEventListener('click', function(event) {
        if (!dropdown.contains(event.target)) {
            dropdownMenu.style.display = 'none';
            dropdownMenu.style.opacity = '0';
            dropdownMenu.style.visibility = 'hidden';
            dropdown.classList.remove('active'); // Aktif sınıfı kaldır
        }
    });

    // Kategoriler dropdown
    document.querySelectorAll('.dropdown > a').forEach(item => {
        item.addEventListener('click', function (e) {
            const parentDropdown = this.parentElement;
            parentDropdown.classList.toggle('active');
            e.stopPropagation(); // Diğer menülerin kapanmaması için
        });
    });

    // Sayfanın herhangi bir yerine tıklanırsa kategoriler menüsünü kapat
    document.addEventListener('click', function () {
        document.querySelectorAll('.dropdown').forEach(item => {
            item.classList.remove('active');
        });
    });

    // Navbar menü öğeleri
    const navLinks = document.querySelectorAll('.nav-links li a');

    // Menüyü tıklanabilir hale getiren fonksiyon
    navLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            // Tüm öğelerden 'active' sınıfını kaldır
            navLinks.forEach(link => link.classList.remove('active'));
            
            // Tıklanan öğeye 'active' sınıfını ekle
            event.target.classList.add('active');
        });
    });



    //mobile menu

   
    const hamburgerMenu = document.getElementById('hamburger-menu');
    const mobileNav = document.getElementById('mobile-nav');

    hamburgerMenu.addEventListener('click', function (e) {
        e.stopPropagation(); // Belgeye tıklanmasını engelle
        mobileNav.style.display = (mobileNav.style.display === 'block') ? 'none' : 'block';
    });

    document.querySelector('.mobile-nav .dropdown > a').addEventListener('click', function(e) {
        e.preventDefault();
        const menu = this.nextElementSibling;
        menu.classList.toggle('show');
    });

    document.querySelectorAll('.mobile-nav .dropdown-submenu > a').forEach(function(item) {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const submenu = this.nextElementSibling;
            submenu.classList.toggle('show');
        });
    });

    // Account menu
    const accountMenu = document.getElementById('account-menu');
    const accountNav = document.getElementById('account-nav');

    accountMenu.addEventListener('click', function (e) {
        e.stopPropagation();
        accountNav.style.display = (accountNav.style.display === 'block') ? 'none' : 'block';
    });

    // Sayfanın başka bir yerine tıklanınca menüleri kapat
    document.addEventListener('click', function (e) {
        // Hamburger menüsü açıksa ve dışına tıklandıysa kapat
        if (mobileNav.style.display === 'block' && !mobileNav.contains(e.target) && e.target !== hamburgerMenu) {
            mobileNav.style.display = 'none';
        }

        // Hesap menüsü açıksa ve dışına tıklandıysa kapat
        if (accountNav.style.display === 'block' && !accountNav.contains(e.target) && e.target !== accountMenu) {
            accountNav.style.display = 'none';
        }
    });
    

   
  


    
}




