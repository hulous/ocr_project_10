import { NgModule, inject } from "@angular/core";
import { CanActivateFn, Router, RouterModule, Routes } from "@angular/router";
import { AuthService } from "./core/services/auth";
import { LoginComponent } from "./pages/login/login";
import { RegisterComponent } from "./pages/register/register";

const authGuard: CanActivateFn = (_route, state) => {
  const authenticationService = inject(AuthService);
  const router = inject(Router);
  return authenticationService.isAuthenticated()
    ? true
    : router.createUrlTree(["/login"], {
        queryParams: { returnUrl: state.url },
      });
};

const routes: Routes = [
  { path: "", redirectTo: "chat", pathMatch: "full" },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  {
    path: "chat",
    canActivate: [authGuard],
    loadChildren: () => import("./chat/chat.module").then((m) => m.ChatModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
