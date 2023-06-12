import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Subject } from "../models/Subject";
import { User } from "../models/User";

const BASE_URL = '/api/subjects/';

interface SubjectDTO {
  name: string;
  students: Number[];
  teachers: Number[];
}
@Injectable({ providedIn: 'root' })
export class SubjectService {
  editSubject(name: string, students: Number[], teachers: Number[], id: number) : Observable<Subject> {
    const subjectDTO: SubjectDTO = {
      name: name,
      students: students,
      teachers: teachers
    };
    return this.http.put(BASE_URL + id, subjectDTO) as Observable<Subject>;
  }

  constructor(private http: HttpClient) { }

  getAllSubjects(name: string, page: number, pageSize : number) : Observable<any> {
    return this.http.get(BASE_URL + '?name=' + name + '&page=' + page + '&size=' + pageSize) as Observable<any>;
  }

  getSubjectById(id: number) : Observable<Subject> {
    return this.http.get(BASE_URL + id) as Observable<Subject>;
  }

  deleteSubjectById(id : Number) : Observable<Subject> {
    return this.http.delete(BASE_URL + id) as Observable<Subject>;
  }

  getSubjectTeachers(id: number, name: string, pageSize: number, page: number) : Observable<any> {
    return this.http.get(BASE_URL + id + '/teachers/?name=' + name + '&page=' + page + '&size=' + pageSize) as Observable<User[]>;
  }

  getSubjectStudents(id: number, name: string, pageSize: number, page: number) : Observable<any> {
    return this.http.get(BASE_URL + id + '/students/?name=' + name + '&page=' + page + '&size=' + pageSize) as Observable<User[]>;
  }

  createSubject(name: string, students: Number[], teachers: Number[]) : Observable<Subject> {
    const subjectDTO: SubjectDTO = {
      name: name,
      students: students,
      teachers: teachers
    };
    return this.http.post(BASE_URL, subjectDTO) as Observable<Subject>;
  }

  getUserSubject(id: Number) : Observable<any> {
    return this.http.get("/api/users/" + id + "/subjects/") as Observable<any>;
  }


}
