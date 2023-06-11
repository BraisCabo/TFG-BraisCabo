import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { User } from "../models/User";
import { Injectable } from "@angular/core";

const BASE_URL = '/api/users/';

@Injectable({ providedIn: 'root' })
export class UserService{
  constructor(private http: HttpClient) { }

  getAllUsers(name: string, pageSize: number, currentPage:number) : Observable<any> {
    return this.http.get(BASE_URL+`?name=${name}&size=${pageSize}&page=${currentPage}`) as Observable<User[]>;
  }
}
