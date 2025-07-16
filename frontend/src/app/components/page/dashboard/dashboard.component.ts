import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [],
    template: `
    <h1>Dashboard</h1>
    <p>Chào mừng {{ username }}!</p>
    
  `
})
export class DashboardComponent implements OnInit {
    username: string = 'người dùng';

    ngOnInit(): void {
        const token = localStorage.getItem('access_token');

        if (token && token.split('.').length === 3) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                this.username = payload.preferred_username || payload.email || 'người dùng';
            } catch (err) {
                console.warn('Token decode fail:', err);
            }
        } else {
            console.warn('Token không hợp lệ hoặc không tồn tại');
        }
    }

}
