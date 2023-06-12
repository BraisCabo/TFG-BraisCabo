import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/User';
import { Observable, catchError, last, throwError } from 'rxjs';

const BASE_URL = '/api/auth/';

interface UserCredentials {
  username: string;
  password: string;
}

interface UserRegister{
  name: string;
  lastName: string;
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private currentUser!: User;
  private logged: boolean = false;

  constructor(private http: HttpClient) {
    this.loadUser();
  }

  login(email: string, password: string): Observable<any> {
    const credentials: UserCredentials = {
      username: email,
      password: password,
    };
    return this.http.post(BASE_URL + 'login', credentials).pipe(
      catchError(() => {
        return throwError(() => new Error('Login start failure'));
      })
    );
  }

  isLogged(): boolean {
    return this.logged;
  }

  getCurrentUser(): User {
    return this.currentUser;
  }

  public loadUser() {
    this.getMe().subscribe((userResponse) => {
      this.currentUser = userResponse;
      this.logged = true;
    });
  }

  private getMe(): Observable<User> {
    return this.http.get('/api/users/me') as Observable<User>;
  }

  isAdmin(): boolean {
    return this.currentUser.roles.includes("ADMIN");
  }

  logout() {
    this.logged = false;
    this.currentUser = null as any;
    this.http.post(BASE_URL + 'logout', {}).subscribe();
  }

  register(name: string, lastname: string, email: string, password: string) : Observable<User> {
    const userRegister : UserRegister = {
      name: name,
      lastName: lastname,
      email: email,
      password: password
    }
    return this.http.post("/api/users/", userRegister) as Observable<User>;
  }
}
