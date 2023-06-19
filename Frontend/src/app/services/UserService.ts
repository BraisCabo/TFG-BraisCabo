import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Injectable } from "@angular/core";
import { UserBasic } from "../models/UserBasic";

const BASE_URL = '/api/users/';

@Injectable({ providedIn: 'root' })
export class UserService{
  constructor(private http: HttpClient) { }

  getAllUsers(name: string, pageSize: number, currentPage:number) : Observable<any> {
    return this.http.get(BASE_URL+`?name=${name}&size=${pageSize}&page=${currentPage}`) as Observable<UserBasic[]>;
  }
}
