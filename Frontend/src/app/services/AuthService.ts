import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, Observer, throwError } from 'rxjs';
import { UserDetailed } from '../models/UserDetailed';
import { UserRegister } from '../models/UserRegister';

const BASE_URL = '/api/auth/';

interface UserCredentials {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private currentUser!: UserDetailed;
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

  getCurrentUser(): UserDetailed {
    return this.currentUser;
  }

  public loadUser() {
    this.getMe().subscribe((userResponse) => {
      this.currentUser = userResponse;
      this.logged = true;
    });
  }

  getMe(): Observable<UserDetailed> {
    return this.http.get('/api/users/me') as Observable<UserDetailed>;
  }

  public getMyUser() {
    return this.http.get('/api/users/me');
  }

  isAdmin(): boolean {
    return this.currentUser.roles.includes("ADMIN");
  }

  logout() {
    this.logged = false;
    this.currentUser = null as any;
    this.http.post(BASE_URL + 'logout', {}).subscribe();
  }

  register(name: string, lastname: string, email: string, password: string) : Observable<UserDetailed> {
    const userRegister : UserRegister = {
      name: name,
      lastName: lastname,
      email: email,
      password: password
    }
    return this.http.post("/api/users/", userRegister) as Observable<UserDetailed>;
  }

  currentUserObserver(): Observable<UserDetailed> {
    let userEmitted = false;

    return new Observable((observer: Observer<UserDetailed>) => {
      if (this.currentUser && !userEmitted) {
        observer.next(this.currentUser);
        userEmitted = true;
      }

      const intervalId = setInterval(() => {
        if (this.currentUser && !userEmitted) {
          observer.next(this.currentUser);
          userEmitted = true;
        }
      }, 1000);

      return () => {
        clearInterval(intervalId);
      };
    });
  }


}
