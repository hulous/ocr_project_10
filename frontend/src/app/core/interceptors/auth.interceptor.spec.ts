import { HttpRequest } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';

describe('AuthInterceptor', () => {
  it('passes authentication requests through without an authorization header', () => {
    const authService = jasmine.createSpyObj('AuthService', ['getToken']);
    authService.getToken.and.returnValue('token');
    const next = jasmine.createSpyObj('HttpHandler', ['handle']);
    const request = new HttpRequest<unknown>('POST', '/api/auth/login', null);

    new AuthInterceptor(authService).intercept(request, next);

    expect(next.handle).toHaveBeenCalledWith(request);
  });

  it('passes requests through when there is no token', () => {
    const authService = jasmine.createSpyObj('AuthService', ['getToken']);
    authService.getToken.and.returnValue(null);
    const next = jasmine.createSpyObj('HttpHandler', ['handle']);
    const request = new HttpRequest('GET', '/api/conversations/demo/messages');

    new AuthInterceptor(authService).intercept(request, next);

    expect(next.handle).toHaveBeenCalledWith(request);
  });

  it('adds the bearer token to authenticated requests', () => {
    const authService = jasmine.createSpyObj('AuthService', ['getToken']);
    authService.getToken.and.returnValue('token');
    const next = jasmine.createSpyObj('HttpHandler', ['handle']);
    const request = new HttpRequest('GET', '/api/conversations/demo/messages');

    new AuthInterceptor(authService).intercept(request, next);

    const forwardedRequest = next.handle.calls.mostRecent().args[0] as HttpRequest<unknown>;
    expect(forwardedRequest.headers.get('Authorization')).toBe('Bearer token');
    expect(forwardedRequest).not.toBe(request);
  });
});
