import { Injectable } from "@angular/core";
import { HttpClient} from '@angular/common/http';
import { User } from "../models/User";
import { Observable, catchError, throwError } from "rxjs";

const BASE_URL = '/api/auth/';

interface UserCredentials {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService{

  private currentUser : User = new User
  private logged : boolean = false

  constructor(private http: HttpClient){}

  login(email:string, password:string){
    const credentials: UserCredentials = {
      username: email,
      password: password,
    };
     this.http.post(BASE_URL+"login", credentials).subscribe(
      _ => {
        this.getMe().subscribe(
          userResponse => {
            this.currentUser = userResponse
            this.logged = true
          }
        )
      }
    )
  }

  isLogged() : boolean {
    return this.logged;
  }

  getCurrentUser() : User{
    return this.currentUser;
  }

  private getMe() : Observable<User>{
    return this.http.get("/api/users/me") as Observable<User>
  }
}
