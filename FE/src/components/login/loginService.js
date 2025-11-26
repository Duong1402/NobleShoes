import axios from 'axios';
import { useAuthStore } from '@/components/login/authStore';
// URL Backend của bạn
const API_URL = 'http://localhost:8080/api/auth/'; 

class AuthService {
    loginCustomer(username, password) {
        const authStore = useAuthStore(); // Dùng store
        return axios.post(API_URL + 'login/customer', { username, password })
            .then(response => {
                const token = response.data.token;
                authStore.setLoginSuccess(token, 'CUSTOMER'); // <-- Báo cho store
                return response.data;
            });
    }

    loginEmployee(username, password) {
        const authStore = useAuthStore(); // Dùng store
        return axios.post(API_URL + 'login/employee', {username, password })
            .then(response => {
                const token = response.data.token;
                authStore.setLoginSuccess(token, 'EMPLOYEE'); // <-- Báo cho store
                return response.data;
            });
    }

    logout() {
        const authStore = useAuthStore();
        authStore.logout(); // <-- Dùng store để logout
    }

    register(data){
        return axios.post(API_URL + 'register', data);
    }
}
export default new AuthService();