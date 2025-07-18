import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../service/auth.service';
import { catchError } from 'rxjs';
import { ToastComponent } from "../../toast/toast.component";

@Component({
    selector: 'app-register',
    standalone: true,
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss'],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        RouterModule,
        ToastComponent
    ]
})
export class RegisterComponent {
    registerForm: FormGroup;
    isSubmitting = false;
    showRegisterError = false;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.registerForm = this.fb.group({
            username: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            firstName: ['', Validators.required],
            lastName: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    onSubmit(): void {
        if (this.registerForm.invalid || this.isSubmitting) return;

        this.isSubmitting = true;
        this.showRegisterError = false;

        this.authService.register(this.registerForm.value)
            .pipe(
                catchError((err) => {
                    this.showRegisterError = true;
                    this.isSubmitting = false;
                    return [];
                })
            )
            .subscribe({
                next: () => {
                    window.alert('Đăng ký thành công!');
                    this.router.navigate(['/login']);
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
    debug() {
        console.log("CLICKED REGISTER");
    }
}
