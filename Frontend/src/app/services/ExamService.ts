import { HttpClient } from "@angular/common/http";
import { Exam } from "../models/Exam";
import { Observable } from "rxjs/internal/Observable";

const BASE_URL = "/api/subjects/"

export class ExamService {
  constructor(private http: HttpClient) { }

  getSubjectExams(subjectId: Number, exam : Exam) : Observable<Exam[]> {
    return this.http.get(BASE_URL + subjectId + "/exams/") as Observable<Exam[]>;
  }

  getExam(subjectId: Number, examId: Number) : Observable<Exam> {
    return this.http.get(BASE_URL + subjectId + "/exams/" + examId) as Observable<Exam>;
  }

  createExam(subjectId: Number, exam: Exam) : Observable<Exam> {
    return this.http.post(BASE_URL + subjectId + "/exams/", exam) as Observable<Exam>;
  }

  updateExam(subjectId: Number, exam: Exam) : Observable<Exam> {
    return this.http.put(BASE_URL + subjectId + "/exams/" + exam.id, exam) as Observable<Exam>;
  }

}
