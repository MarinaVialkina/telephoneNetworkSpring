class PhoneNetworkApp {
    constructor() {
        this.phones = [];
        this.baseUrl = '/api/phones';
        this.eventSource = null;
        this.init();
    }

    async init() {
        await this.loadPhones();
        this.startSSE();
        this.renderPhones();
        this.attachGlobalEventListeners();
    }

    async loadPhones() {
        try {
            const response = await fetch(this.baseUrl);
            this.phones = await response.json();
        } catch (error) {
            this.showError('Ошибка загрузки телефонов');
            console.error('Error loading phones:', error);
        }
    }

    startSSE() {
        this.eventSource = new EventSource('/api/events/phones');

        this.eventSource.addEventListener('phones-update', (event) => {
            const updatedPhones = JSON.parse(event.data);
            this.phones = updatedPhones;
            this.renderPhones();
            console.log('Phones updated via SSE');
        });

        this.eventSource.onopen = () => {
            console.log('SSE connection established');
        };

        this.eventSource.onerror = (error) => {
            console.error('SSE Error:', error);
        };
    }

    renderPhones() {
        const container = document.getElementById('phones-container');

        if (this.phones.length === 0) {
            container.innerHTML = '<div class="loading"> Телефонная сеть пуста. Добавьте первый телефон!</div>';
            return;
        }

        const sortedPhones = [...this.phones].sort((a, b) => a.phoneNumber.localeCompare(b.phoneNumber));

        container.innerHTML = sortedPhones.map(phone => `
            <div class="phone-card">
                <div class="phone-image-container">
                    <img src="${this.getImageForStatus(phone.status)}"
                         alt="Phone ${phone.phoneNumber}" class="phone-image"
                         onerror="this.src='/images/phone.jpg';">
                </div>
                <div class="phone-number">${phone.phoneNumber}</div>
                <div class="phone-status ${this.getStatusClass(phone.status)}">
                    ${this.getStatusText(phone.status)}
                </div>
                <div class="phone-actions">
                    ${this.getButtonsForStatus(phone)}
                </div>
            </div>
        `).join('');

        this.attachEventListeners();
    }

    getImageForStatus(status) {
        const images = {
            'FREE': '/images/phone.jpg',
            'RINGING': '/images/call.jpg',
            'BUSY': '/images/answer.jpg'
        };
        return images[status] || '/images/phone.jpg';
    }

    getStatusText(status) {
        const statusTexts = {
            'FREE': 'Свободен',
            'RINGING': 'Звонит',
            'BUSY': 'Занят'
        };
        return statusTexts[status] || status;
    }

    getStatusClass(status) {
        return `status-${status.toLowerCase()}`;
    }

    getButtonsForStatus(phone) {
        switch(phone.status) {
            case 'FREE':
                return `
                    <button class="btn btn-call" data-number="${phone.phoneNumber}" data-action="call">
                         Позвонить
                    </button>
                `;
            case 'RINGING':
                return `
                    <button class="btn btn-answer" data-number="${phone.phoneNumber}" data-action="answer">
                         Принять
                    </button>
                    <button class="btn btn-reject" data-number="${phone.phoneNumber}" data-action="terminate">
                         Отклонить
                    </button>
                `;
            case 'BUSY':
                return `
                    <button class="btn btn-reject" data-number="${phone.phoneNumber}" data-action="terminate">
                         Завершить
                    </button>
                `;
            default:
                return '';
        }
    }

    attachEventListeners() {
        document.querySelectorAll('.btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const phoneNumber = e.target.dataset.number;
                const action = e.target.dataset.action;
                this.handleAction(phoneNumber, action);
            });
        });
    }

    attachGlobalEventListeners() {
        const addButton = document.getElementById('add-phone-btn');
        if (addButton) {
            addButton.addEventListener('click', () => {
                this.addPhone();
            });
        }

        const deleteButton = document.getElementById('delete-phone-btn');
        if (deleteButton) {
            deleteButton.addEventListener('click', () => {
                this.deletePhoneFromHeader();
            });
        }
    }

    async deletePhoneFromHeader() {
        const phoneNumber = prompt('Введите номер телефона для удаления:');
        if (!phoneNumber) return;

        if (!/^\d{4,}$/.test(phoneNumber)) {
            this.showError('Номер телефона должен содержать только цифры (минимум 4)');
            return;
        }

        await this.deletePhone(phoneNumber);
    }

    async handleAction(phoneNumber, action) {
        try {
            let url;
            let body = null;
            let response;

            switch(action) {
                case 'call':
                    const targetNumber = prompt('Введите номер для звонка:');
                    if (!targetNumber) return;
                    if (targetNumber === phoneNumber) {
                        this.showError('Нельзя звонить на свой же номер');
                        return;
                    }

                    url = `/api/calls/${phoneNumber}/to/${targetNumber}`;
                    break;

                case 'answer':
                    url = `/api/calls/${phoneNumber}/answer`;
                    break;

                case 'terminate':
                    url = `/api/calls/${phoneNumber}/terminate`;
                    break;
            }

            response = await fetch(url, {
                method: 'POST',
                headers: body ? { 'Content-Type': 'application/json' } : {},
                body: body
            });

            const result = await response.json();

            if (!result.success) {
                this.showError(result.message);
            } else {
                this.showSuccess('Действие выполнено успешно');
            }



        } catch (error) {
            this.showError('Ошибка соединения с сервером');
            console.error('Error performing action:', error);
        }
    }

    async addPhone() {
        const phoneNumber = prompt('Введите номер нового телефона:');
        if (!phoneNumber) return;

        try {
            const response = await fetch(this.baseUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ phoneNumber: phoneNumber })
            });

            if (response.ok) {
                const newPhone = await response.json();
                this.showSuccess(`Телефон ${phoneNumber} добавлен успешно`);
                // УБРАТЬ принудительное обновление - SSE сделает это автоматически
                // await this.loadPhones();
                // this.renderPhones();
            } else {
                const result = await response.json();
                this.showError(result.message || 'Ошибка добавления телефона');
            }
        } catch (error) {
            this.showError('Ошибка соединения с сервером');
            console.error('Error adding phone:', error);
        }
    }

    async deletePhone(phoneNumber) {
        if (!confirm(`Вы уверены, что хотите удалить телефон ${phoneNumber}?`)) return;

        try {
            const response = await fetch(`${this.baseUrl}/${phoneNumber}`, {
                method: 'DELETE'
            });

            const result = await response.json();
            if (result.success) {
                this.showSuccess(`Телефон ${phoneNumber} удален`);
                // УБРАТЬ принудительное обновление - SSE сделает это автоматически
                // await this.loadPhones();
                // this.renderPhones();
            } else {
                this.showError(result.message || 'Ошибка удаления телефона');
            }
        } catch (error) {
            this.showError('Ошибка соединения с сервером');
            console.error('Error deleting phone:', error);
        }
    }

    showError(message) {
        this.showMessage(message, 'error');
    }

    showSuccess(message) {
        this.showMessage(message, 'success');
    }

    showMessage(message, type = 'error') {
        const toast = document.getElementById('error-toast');
        toast.textContent = message;
        toast.className = `toast ${type}`;
        toast.classList.remove('hidden');

        setTimeout(() => {
            toast.classList.add('hidden');
        }, 5000);
    }

    // Опционально: метод для закрытия соединения при необходимости
    destroy() {
        if (this.eventSource) {
            this.eventSource.close();
            console.log('SSE connection closed');
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.phoneApp = new PhoneNetworkApp();
});