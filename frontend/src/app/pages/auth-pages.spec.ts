import { ActivatedRoute } from "@angular/router";
import { of, throwError } from "rxjs";
import { LoginComponent } from "./login/login";
import { RegisterComponent } from "./register/register";

describe("Authentication page components", () => {
  it("does not submit login with incomplete or duplicate input", () => {
    const auth = jasmine.createSpyObj("AuthService", ["login"]);
    const router = jasmine.createSpyObj("Router", ["navigateByUrl"]);
    const route = {} as ActivatedRoute;
    const component = new LoginComponent(auth, router, route);

    component.submit();
    component.email = "client@example.com";
    component.password = "secret";
    component.isSubmitting = true;
    component.submit();

    expect(auth.login).not.toHaveBeenCalled();
  });

  it("logs in and navigates to the requested return URL", () => {
    const auth = jasmine.createSpyObj("AuthService", ["login"]);
    auth.login.and.returnValue(of({ token: "token", expiresIn: 3600 }));
    const router = jasmine.createSpyObj("Router", ["navigateByUrl"]);
    const route = {
      snapshot: { queryParamMap: { get: () => "/chat?conversationId=1" } },
    } as unknown as ActivatedRoute;
    const component = new LoginComponent(auth, router, route);
    component.email = "client@example.com";
    component.password = "secret";

    component.submit();

    expect(router.navigateByUrl).toHaveBeenCalledWith("/chat?conversationId=1");
  });

  it("reports login errors and falls back to chat when no return URL exists", () => {
    const auth = jasmine.createSpyObj("AuthService", ["login"]);
    const router = jasmine.createSpyObj("Router", ["navigateByUrl"]);
    const route = {
      snapshot: { queryParamMap: { get: () => null } },
    } as unknown as ActivatedRoute;
    const component = new LoginComponent(auth, router, route);
    component.email = "client@example.com";
    component.password = "secret";
    auth.login.and.returnValue(throwError(() => ({ status: 401 })));

    component.submit();

    expect(component.errorMessage).toBe(
      "Adresse email ou mot de passe incorrect.",
    );
    expect(component.isSubmitting).toBeFalse();

    auth.login.and.returnValue(of({ token: "token", expiresIn: 3600 }));
    component.submit();
    expect(router.navigateByUrl).toHaveBeenCalledWith("/chat");
  });

  it("validates registration, registers, and navigates to login", () => {
    const auth = jasmine.createSpyObj("AuthService", ["register"]);
    auth.register.and.returnValue(
      of({ id: "user-1", name: "Client", email: "client@example.com" }),
    );
    const router = jasmine.createSpyObj("Router", ["navigate"]);
    const component = new RegisterComponent(auth, router);

    component.name = "Client";
    component.email = "client@example.com";
    component.password = "long-enough-password";
    component.submit();

    expect(auth.register).toHaveBeenCalledWith({
      name: "Client",
      email: "client@example.com",
      password: "long-enough-password",
    });
    expect(router.navigate).toHaveBeenCalledWith(["/login"]);
  });

  it("reports registration errors and rejects invalid or duplicate submissions", () => {
    const auth = jasmine.createSpyObj("AuthService", ["register"]);
    const router = jasmine.createSpyObj("Router", ["navigate"]);
    const component = new RegisterComponent(auth, router);

    component.name = "Client";
    component.email = "client@example.com";
    component.password = "short";
    component.submit();
    expect(auth.register).not.toHaveBeenCalled();

    component.password = "long-enough-password";
    component.isSubmitting = true;
    component.submit();
    expect(auth.register).not.toHaveBeenCalled();

    component.isSubmitting = false;
    auth.register.and.returnValue(throwError(() => ({ status: 400 })));
    component.submit();
    expect(component.errorMessage).toBe("Vérifiez les informations saisies.");
    expect(component.isSubmitting).toBeFalse();
  });
});
