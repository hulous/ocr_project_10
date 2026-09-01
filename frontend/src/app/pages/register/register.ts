import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-register',
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private readonly authenticationService: AuthService,
    private readonly router: Router,
  ) {}

  submit(): void {
    if (this.isSubmitting || !this.name || !this.email || this.password.length < 8) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.authenticationService.register({ name: this.name, email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = error.status === 400
          ? 'Vérifiez les informations saisies.'
          : 'La création du compte est momentanément indisponible.';
      },
    });
  }
}
