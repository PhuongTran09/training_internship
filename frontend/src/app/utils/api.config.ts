export const environment = {
  production: false
};

export const apiUrl = {
  BASE_URL: 'http://localhost:8081/api'
};


const endpoint = (path: string) => `${apiUrl.BASE_URL}/${path}`;

export const API = {
  AUTH: {
    LOGIN: endpoint('auth/login'),
    LOGOUT: endpoint('auth/logout')
  },
  USER: {
    PROFILE: endpoint('user/profile')
  }
};
