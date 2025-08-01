import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../../service/auth.service';
import {catchError} from 'rxjs';
import {ToastComponent} from "../../toast/toast.component";

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

  onSubmit() {
    if (this.registerForm.invalid) return;
    const username = this.registerForm.value.username;
    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        window.alert('Đăng ký thành công! Vui lòng đăng nhập.');
        this.router.navigate(['/login'], {queryParams: {username}});
      },
      error: (err) => {
        window.alert('Đăng ký thất bại: ' + err.message);
      },
    });
  }

  showPassword = false;
  passwordFocused = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
