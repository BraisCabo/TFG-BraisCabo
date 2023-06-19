import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs/internal/Observable";
import { Injectable } from "@angular/core";
import { ExamBasic } from "../models/ExamBasic";
import { ExamTeacher } from "../models/ExamTeacher";

const BASE_URL = "/api/subjects/"

@Injectable({ providedIn: 'root' })
export class ExamService {
  constructor(private http: HttpClient) { }

  getSubjectExams(subjectId: Number) : Observable<ExamBasic[]> {
    return this.http.get(BASE_URL + subjectId + "/exams/") as Observable<ExamBasic[]>;
  }

  getExam(subjectId: Number, examId: Number) : Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/exams/" + examId) as Observable<any>;
  }

  getQuestions(subjectId: Number, examId: Number) : Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/exams/" + examId + "/questions") as Observable<any>;
  }

  createExam(subjectId: Number, exam: ExamTeacher) : Observable<ExamTeacher> {
    console.log(exam.closingDate)
    console.log(exam.openingDate)
    return this.http.post(BASE_URL + subjectId + "/exams/", exam) as Observable<ExamTeacher>;
  }

  updateExam(subjectId: Number, exam: ExamTeacher) : Observable<ExamTeacher> {
    console.log(subjectId)
    console.log(exam)
    return this.http.put(BASE_URL + subjectId + "/exams/" + exam.id, exam) as Observable<ExamTeacher>;
  }

  changeVisibility(subjectId: Number, examId: Number, newVisibility : Boolean) : Observable<any> {

    return this.http.patch(BASE_URL + subjectId + "/exams/" + examId, newVisibility) as Observable<any>;
  }

}
