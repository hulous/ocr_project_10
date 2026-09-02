import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth';

describe('AuthenticationService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('posts credentials and stores only the authentication response and email', () => {
    const credentials = { email: 'client@example.com', password: 'secret' };
    const response = { token: 'jwt-token', expiresIn: 3600 };

    service.login(credentials).subscribe((loginResponse) => {
      expect(loginResponse).toEqual(response);
      expect(service.getToken()).toBe(response.token);
      expect(service.getEmail()).toBe(credentials.email);
      expect(JSON.parse(localStorage.getItem('ycyw.authentication')!)).toEqual({
        ...response,
        email: credentials.email,
      });
    });

    const request = httpTestingController.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(credentials);
    request.flush(response);
  });

  it('clears the stored authentication on logout', () => {
    localStorage.setItem(
      'ycyw.authentication',
      JSON.stringify({
        token: 'jwt-token',
        expiresIn: 3600,
        email: 'client@example.com',
      }),
    );

    service.logout();

    expect(service.isAuthenticated()).toBeFalse();
    expect(service.getEmail()).toBeNull();
  });
});
