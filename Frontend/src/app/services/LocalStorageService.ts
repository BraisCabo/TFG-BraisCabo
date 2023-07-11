import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

const BASE_URL = "/api/subjects/"

@Injectable({ providedIn: 'root' })
export class LocalStorageService {
  constructor() {}

  public setItem(key: string, value: string){
    localStorage.setItem(key, value);
  }

  public getItem(key: string) : string {
    const value = localStorage.getItem(key);
    if (value == null) {
      return "";
    }
    return value;
  }
}
