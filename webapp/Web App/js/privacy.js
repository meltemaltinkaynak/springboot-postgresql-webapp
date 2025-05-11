document.getElementById('manageSessionsBtn').addEventListener('click', async function () {
    document.getElementById('manageSessionsModal').style.display = 'block';

    try {
        const response = await fetch('https://localhost:8443/api/sessions/user', {
            method: 'GET',
            credentials: 'include'
        });

        const jsonResponse = await response.json();

        if (jsonResponse.result) {
            const sessionsList = document.getElementById('sessionsList');
            sessionsList.innerHTML = '';

            jsonResponse.data.forEach(session => {
                const li = document.createElement('li');

                // Oturum bilgileri metni
                let sessionInfo = `
                    <p><strong>IP:</strong> ${session.ipAddress}</p>
                    <p><strong>Cihaz:</strong> ${session.deviceName}</p>
                    <p><strong>Giriş Zamanı:</strong> ${new Date(session.loginTime).toLocaleString()}</p>
                    <p><strong>Oturum Durumu:</strong> ${session.active ? 'Aktif' : 'Sonlandırıldı'}</p>
                `;

                li.innerHTML = sessionInfo;

                // Eğer oturum aktifse, "Oturumu Sonlandır" butonunu ekle
                if (session.active) {
                    const terminateBtn = document.createElement('button');
                    terminateBtn.textContent = 'Oturumu Sonlandır';
                    terminateBtn.addEventListener('click', () => terminateSession(session.id));

                    li.appendChild(document.createTextNode(' ')); // boşluk bırak
                    li.appendChild(terminateBtn);
                }

                sessionsList.appendChild(li);
            });
        } else {
            showToastMessage('Oturumlar getirilemedi: ' + jsonResponse.errorMessage);
        }
    } catch (error) {
        console.error('Fetch error:', error);
    }
});

async function terminateSession(sessionId) {
    try {
        const response = await fetch(`https://localhost:8443/api/sessions/terminate/${sessionId}`, {
            method: 'POST',
            credentials: 'include'
        });

        const jsonResponse = await response.json();

        if (jsonResponse.result) {
            showToastMessage('Oturum başarıyla sonlandırıldı.', true);
           
        } else {
            showToastMessage(jsonResponse.errorMessage, false);
        }
    } catch (error) {
        console.error('Error terminating session:', error);
        showToastMessage('Bir hata oluştu. Lütfen tekrar deneyin.', false);
    }
}


// Modal açıldığında
function openModal() {
    document.getElementById('manageSessionsModal').style.display = 'block';
    document.body.style.overflow = 'hidden'; // Sayfa kaydırılmasını engelle
}

// Modal kapandığında
function closeModal() {
    document.getElementById('manageSessionsModal').style.display = 'none';
    document.body.style.overflow = 'auto'; // Sayfa kaydırılmasını geri aç
}

// Modal'ı açan buton
document.getElementById('manageSessionsBtn').addEventListener('click', openModal);

// Modal'ı kapatan span öğesi
document.getElementById('close-modal').addEventListener('click', closeModal);
