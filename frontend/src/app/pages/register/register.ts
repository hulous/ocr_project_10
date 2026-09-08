import { HttpErrorResponse } from "@angular/common/http";
import { Component, signal } from "@angular/core";
import { Router } from "@angular/router";

import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../core/services/auth";

@Component({
  selector: "app-register",
  imports: [FormsModule, RouterLink],
  templateUrl: "./register.html",
})
export class RegisterComponent {
  name = signal("");
  email = signal("");
  password = signal("");
  errorMessage = signal("");
  isSubmitting = signal(false);

  constructor(
    private readonly authenticationService: AuthService,
    private readonly router: Router,
  ) {}

  submit(): void {
    if (
      this.isSubmitting() ||
      !this.name() ||
      !this.email() ||
      this.password().length < 8
    ) {
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set("");
    this.authenticationService
      .register({ name: this.name(), email: this.email(), password: this.password() })
      .subscribe({
        next: () => this.router.navigate(["/login"]),
        error: (error: HttpErrorResponse) => {
          this.isSubmitting.set(false);
          this.errorMessage.set(
            error.status === 400
              ? "Vérifiez les informations saisies."
              : "La création du compte est momentanément indisponible.",
          );
        },
      });
  }
}
