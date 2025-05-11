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
            const role = user.role;

            

            const loginBtn = document.querySelector(".auth-buttons .login-btn");
            const registerBtn = document.querySelector(".auth-buttons .register-btn");
            if (loginBtn) loginBtn.style.display = "none";
            if (registerBtn) registerBtn.style.display = "none";

            const userName = document.getElementById("user-name");
            const userInfo = document.getElementById("user-info");
            if (userName) userName.textContent = `${user.firstName} ${user.lastName}`;
            if (userInfo) userInfo.style.display = "flex";

            const accountMenu = document.getElementById("account-menu");
            if (accountMenu) {
                const screenWidth = window.innerWidth;
                if (screenWidth > 768) {
                    accountMenu.style.display = "none";
                } else {
                    accountMenu.style.display = "block";
                }
            }

            const mobileLogin = document.querySelector('#mobile-nav a[href="login.html"]');
            const mobileRegister = document.querySelector('#mobile-nav a[href="register.html"]');
            if (mobileLogin) mobileLogin.parentElement.style.display = "none";
            if (mobileRegister) mobileRegister.parentElement.style.display = "none";

            if (role === "CONTENTADMIN" || role === "SUPERADMIN") {
                const panelUrl = role === "SUPERADMIN" ? "superadminpanel.html" : "contentadminpanel.html";

                const desktopMenu = document.querySelector(".user-dropdown .dropdown-menu");
                if (desktopMenu) {
                    const adminLi = document.createElement("li");
                    adminLi.innerHTML = `<a href="${panelUrl}">Admin Panel</a>`;
                    desktopMenu.insertBefore(adminLi, desktopMenu.firstChild);
                }

                const mobileMenu = document.getElementById("mobile-menu");
                if (mobileMenu) {
                    const adminLiMobile = document.createElement("li");
                    adminLiMobile.innerHTML = `<a href="${panelUrl}">Admin Panel</a>`;
                    mobileMenu.insertBefore(adminLiMobile, mobileMenu.firstChild);
                }
            }
        }
    })
    .catch(error => console.error("Kullanıcı bilgisi alınamadı:", error));
}

function initNavbarScripts() {
    const dropdown = document.querySelector('.user-dropdown');
    const dropdownMenu = dropdown.querySelector('.dropdown-menu');

    dropdown.addEventListener('click', function(event) {
        event.stopPropagation();
        if (dropdownMenu.style.display === 'block') {
            dropdownMenu.style.display = 'none';
            dropdownMenu.style.opacity = '0';
            dropdownMenu.style.visibility = 'hidden';
        } else {
            dropdownMenu.style.display = 'block';
            dropdownMenu.style.opacity = '1';
            dropdownMenu.style.visibility = 'visible';
        }
        dropdown.classList.toggle('active');
    });

    document.addEventListener('click', function(event) {
        if (!dropdown.contains(event.target)) {
            dropdownMenu.style.display = 'none';
            dropdownMenu.style.opacity = '0';
            dropdownMenu.style.visibility = 'hidden';
            dropdown.classList.remove('active');
        }
    });

    document.querySelectorAll('.dropdown > a').forEach(item => {
        item.addEventListener('click', function (e) {
            const parentDropdown = this.parentElement;
            parentDropdown.classList.toggle('active');
            e.stopPropagation();
        });
    });

    document.addEventListener('click', function () {
        document.querySelectorAll('.dropdown').forEach(item => {
            item.classList.remove('active');
        });
    });

    const navLinks = document.querySelectorAll('.nav-links li a');
    navLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            navLinks.forEach(link => link.classList.remove('active'));
            event.target.classList.add('active');
        });
    });

    const hamburgerMenu = document.getElementById('hamburger-menu');
    const mobileNav = document.getElementById('mobile-nav');
    const overlay = document.getElementById('overlay');

    hamburgerMenu.addEventListener('click', function (e) {
        e.stopPropagation();
    
        // Diğer menüyü kapat
        accountNav.style.display = 'none';
        accountMenu.classList.remove('active');
    
        const isVisible = mobileNav.style.display === 'block';
        mobileNav.style.display = isVisible ? 'none' : 'block';
        this.classList.toggle('active', !isVisible); // aktiflik kontrolü
        overlay.style.display = isVisible ? 'none' : 'block';
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

    const accountMenu = document.getElementById('account-menu');
    const accountNav = document.getElementById('account-nav');

    accountMenu.addEventListener('click', function (e) {
        e.stopPropagation();
    
        // Diğer menüyü kapat
        mobileNav.style.display = 'none';
        hamburgerMenu.classList.remove('active');
    
        const isVisible = accountNav.style.display === 'block';
        accountNav.style.display = isVisible ? 'none' : 'block';
        this.classList.toggle('active', !isVisible); 
        overlay.style.display = isVisible ? 'none' : 'block';
    });
    
    document.addEventListener('click', function (e) {
        if (mobileNav.style.display === 'block' && !mobileNav.contains(e.target) && e.target !== hamburgerMenu) {
            mobileNav.style.display = 'none';
            hamburgerMenu.classList.remove('active'); 
            overlay.style.display = 'none';
        }
    
        if (accountNav.style.display === 'block' && !accountNav.contains(e.target) && e.target !== accountMenu) {
            accountNav.style.display = 'none';
            accountMenu.classList.remove('active'); 
            overlay.style.display = 'none';
        }
    });
}





