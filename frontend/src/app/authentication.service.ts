import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
}

interface StoredAuthentication extends LoginResponse {
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private readonly storageKey = 'ycyw.authentication';
  private readonly loginUrl = '/api/auth/login';

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginCredentials): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, credentials).pipe(
      tap((response) => {
        this.storeAuthentication({ ...response, email: credentials.email });
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }

  getToken(): string | null {
    return this.getStoredAuthentication()?.token ?? null;
  }

  getEmail(): string | null {
    return this.getStoredAuthentication()?.email ?? null;
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  private storeAuthentication(authentication: StoredAuthentication): void {
    localStorage.setItem(this.storageKey, JSON.stringify(authentication));
  }

  private getStoredAuthentication(): StoredAuthentication | null {
    const storedAuthentication = localStorage.getItem(this.storageKey);
    if (!storedAuthentication) {
      return null;
    }

    try {
      return JSON.parse(storedAuthentication) as StoredAuthentication;
    } catch {
      this.logout();
      return null;
    }
  }
}