import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../service/auth.service';
import { catchError } from 'rxjs';
import { ToastComponent } from "../../toast/toast.component";

@Component({
    selector: 'app-login',
    standalone: true,
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        RouterModule,
        ToastComponent
    ]
})
export class LoginComponent {
    loginForm: FormGroup;
    isSubmitting = false;
    showLoginError = false;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.loginForm = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    onSubmit(): void {
        if (this.loginForm.invalid || this.isSubmitting) return;

        this.isSubmitting = true;
        this.showLoginError = false;

        this.authService.login(this.loginForm.value)
            .pipe(
                catchError((err) => {
                    this.showLoginError = true;
                    this.isSubmitting = false;
                    return [];
                })
            )
            .subscribe({
                next: () => {
                    window.alert('Đăng nhập thành công');
                    this.router.navigate(['/dashboard']);
                },
                complete: () => {
                    this.isSubmitting = false;
                }
            });
    }

    showPassword = false;
    passwordFocused = false;

    togglePasswordVisibility() {
        this.showPassword = !this.showPassword;
    }
}
