import { HttpClient } from "@angular/common/http";
import { Exam } from "../models/Exam";
import { Observable } from "rxjs/internal/Observable";
import { Injectable } from "@angular/core";

const BASE_URL = "/api/subjects/"

export interface ExamDTO {
  name : string
  calificationPercentaje : number
  visibleExam : boolean
  calificationVisible : boolean
  openingDate : Date
  closingDate : Date
  type : string
  questions : string[]
}

@Injectable({ providedIn: 'root' })
export class ExamService {
  constructor(private http: HttpClient) { }

  getSubjectExams(subjectId: Number) : Observable<Exam[]> {
    return this.http.get(BASE_URL + subjectId + "/exams/") as Observable<Exam[]>;
  }

  getExams(subjectId: Number, examId: Number) : Observable<Exam> {
    return this.http.get(BASE_URL + subjectId + "/exams/" + examId) as Observable<Exam>;
  }

  createExam(subjectId: Number, exam: ExamDTO) : Observable<Exam> {
    console.log(exam.closingDate)
    return this.http.post(BASE_URL + subjectId + "/exams/", exam) as Observable<Exam>;
  }

  updateExam(subjectId: Number, exam: Exam) : Observable<Exam> {
    console.log(subjectId)
    console.log(exam)
    return this.http.put(BASE_URL + subjectId + "/exams/" + exam.id, exam) as Observable<Exam>;
  }

}
