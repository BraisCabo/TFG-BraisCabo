import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { SubjectChanges } from "../models/SubjectChanges";
import { SubjectDetailed } from "../models/SubjectDetailed";
import { UserBasic } from "../models/UserBasic";

const BASE_URL = '/api/subjects/';

@Injectable({ providedIn: 'root' })
export class SubjectService {

  editSubject(name: string, students: Number[], teachers: Number[], id: number) : Observable<SubjectDetailed> {
    const subjectDTO: SubjectChanges = {
      name: name,
      students: students,
      teachers: teachers
    };
    return this.http.put(BASE_URL + id, subjectDTO) as Observable<SubjectDetailed>;
  }

  constructor(private http: HttpClient) { }

  getAllSubjects(name: string, page: number, pageSize : number) : Observable<any> {
    return this.http.get(BASE_URL + '?name=' + name + '&page=' + page + '&size=' + pageSize) as Observable<any>;
  }

  getSubjectById(id: number) : Observable<SubjectDetailed> {
    return this.http.get(BASE_URL + id) as Observable<SubjectDetailed>;
  }

  deleteSubjectById(id : Number) : Observable<SubjectDetailed> {
    return this.http.delete(BASE_URL + id) as Observable<SubjectDetailed>;
  }

  getSubjectTeachers(id: number, name: string, pageSize: number, page: number) : Observable<any> {
    return this.http.get(BASE_URL + id + '/teachers/?name=' + name + '&page=' + page + '&size=' + pageSize) as Observable<UserBasic[]>;
  }

  getSubjectStudents(id: number, name: string, pageSize: number, page: number) : Observable<any> {
    return this.http.get(BASE_URL + id + '/students/?name=' + name + '&page=' + page + '&size=' + pageSize) as Observable<UserBasic[]>;
  }

  createSubject(name: string, students: Number[], teachers: Number[]) : Observable<SubjectDetailed> {
    const subjectDTO: SubjectChanges = {
      name: name,
      students: students,
      teachers: teachers
    };
    return this.http.post(BASE_URL, subjectDTO) as Observable<SubjectDetailed>;
  }

  getUserSubject(id: Number) : Observable<any> {
    return this.http.get("/api/users/" + id + "/subjects/") as Observable<any>;
  }

  getSubjectExams(userId: Number, subjectId: Number) : Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/users" + userId + "/") as Observable<any>;
  }

  isSubjectTeacher(userId: Number, subjectId: Number) : Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/users/" + userId + "/") as Observable<any>;
  }

  getStudentCalifications(userId: number, subjectId: number): Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/users/" + userId + "/califications") as Observable<any>;
  }

  getTeacherSubjectCalifications(subjectId: number): Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/califications") as Observable<any>;
  }
}
