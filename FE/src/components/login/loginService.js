import axios from 'axios';
import { useAuthStore } from '@/components/login/authStore';
// URL Backend của bạn
const API_URL = 'http://localhost:8080/api/auth/'; 

class AuthService {
    
    // Hàm giúp giải mã token để lấy quyền ngay lập tức
    _decodeToken(token) {
        try {
            return jwtDecode(token);
        } catch (e) {
            return null;
        }
    }

    loginCustomer(username, password) {
        const authStore = useAuthStore();
        return axios.post(API_URL + 'login/customer', { username, password })
            .then(response => {
                const token = response.data.token;
                
                // Lấy UserType từ token ngay sau khi tạo
                const payload = this._decodeToken(token);
                const userType = payload ? payload.userType : 'CUSTOMER'; 
                
                authStore.setLoginSuccess(token, userType);
                return response.data;
            });
    }

    loginEmployee(username, password) {
        const authStore = useAuthStore();
        return axios.post(API_URL + 'login/employee', { username, password })
            .then(response => {
                const token = response.data.token;
                
                // Lấy UserType từ token ngay sau khi tạo
                const payload = this._decodeToken(token);
                const userType = payload ? payload.userType : 'EMPLOYEE';
                
                authStore.setLoginSuccess(token, userType);
                return response.data;
            });
    }

    register(data){
        // Mặc dù đây là register, nhưng nó cần sử dụng token nếu bạn có bảo mật cho /register
        return axios.post(API_URL + 'register', data);
    }
    
    logout() {
        const authStore = useAuthStore();
        authStore.logout();
    }
    
    // === HÀM BỔ SUNG CHO PHÂN QUYỀN TRÊN FRONTEND ===
    
    /**
     * Kiểm tra xem người dùng hiện tại có quyền hạn tối thiểu yêu cầu không.
     * @param {string[]} requiredRoles - Danh sách các quyền được yêu cầu (vd: ['ADMIN', 'EMPLOYEE'])
     * @returns {boolean}
     */
    checkAuthority(requiredRoles) {
        const authStore = useAuthStore();
        if (!authStore.isLoggedIn) return false;
        
        // Lấy role hiện tại từ Store (ví dụ: 'EMPLOYEE')
        const currentUserRole = authStore.userType; 
        if (!currentUserRole) return false;

        // Chuẩn hóa và kiểm tra
        const normalizedCurrentUserRole = currentUserRole.toUpperCase();
        
        return requiredRoles.some(requiredRole => 
            normalizedCurrentUserRole === requiredRole.toUpperCase()
        );
    }
}
export default new AuthService();