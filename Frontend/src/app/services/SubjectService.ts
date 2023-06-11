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

  constructor(private http: HttpClient) { }

  getAllSubjects() : Observable<Subject[]> {
    return this.http.get(BASE_URL) as Observable<Subject[]>;
  }

  getSubjectById(id: number) : Observable<Subject> {
    return this.http.get(BASE_URL + id) as Observable<Subject>;
  }

  deleteSubjectById(id : Number) : Observable<Subject> {
    return this.http.delete(BASE_URL + id) as Observable<Subject>;
  }

  createSubject(name: string, students: User[], teachers : User[]) : Observable<Subject> {
    const subjectDTO: SubjectDTO = {
      name: name,
      students: students.map(student => student.id),
      teachers: teachers.map(teacher => teacher.id)
    };
    return this.http.post(BASE_URL, subjectDTO) as Observable<Subject>;
  }


}
