import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs/internal/Observable";
import { Injectable } from "@angular/core";
import { ExamBasic } from "../models/ExamBasic";
import { ExamTeacher } from "../models/ExamTeacher";
import { ExamChanges } from "../models/ExamChanges";
import { ImportedExam } from "../models/ImportedExam";

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

  getFiles(subjectId: Number, examId: Number) : Observable<any> {
    return this.http.get(BASE_URL + subjectId + "/exams/" + examId + "/files", {responseType: 'blob', observe: 'response' }) as Observable<any>;
  }

  createExam(subjectId: Number, exam: ExamChanges, examFile: File) : Observable<ExamTeacher> {
    const formData : FormData = new FormData();
    formData.append("id", Number(0).toString());
    formData.append("name", exam.name);
    formData.append("type", exam.type);
    formData.append("calificationPercentaje", exam.calificationPercentaje.toString());
    formData.append("visibleExam", exam.visibleExam.toString());
    formData.append("calificationVisible", exam.calificationVisible.toString());
    formData.append("openingDate", exam.openingDate.toString());
    formData.append("closingDate", exam.closingDate.toString());
    formData.append("examFile", examFile);
    formData.append("questions", exam.questions.toString());
    formData.append("canRepeat", exam.canRepeat.toString());
    formData.append("canUploadLate", exam.canUploadLate.toString());
    formData.append("questionsCalifications", exam.questionsCalifications.toString())
    formData.append("maxTime", exam.maxTime.toString())


    return this.http.post(BASE_URL + subjectId + "/exams/", formData) as Observable<ExamTeacher>;
  }

  updateExam(subjectId: Number, exam: ExamChanges) : Observable<ExamTeacher> {
    const formData : FormData = new FormData();
    formData.append("id", exam.id.toString());
    formData.append("name", exam.name);
    formData.append("type", exam.type);
    formData.append("calificationPercentaje", exam.calificationPercentaje.toString());
    formData.append("visibleExam", exam.visibleExam.toString());
    formData.append("calificationVisible", exam.calificationVisible.toString());
    formData.append("openingDate", exam.openingDate.toString());
    formData.append("closingDate", exam.closingDate.toString());
    formData.append("examFile", exam.examFile);
    formData.append("questions", exam.questions.toString());
    formData.append("deletedFile", exam.deletedFile.toString());
    formData.append("canRepeat", exam.canRepeat.toString());
    formData.append("canUploadLate", exam.canUploadLate.toString());
    formData.append("questionsCalifications", exam.questionsCalifications.toString())
    formData.append("maxTime", exam.maxTime.toString())

    return this.http.put(BASE_URL + subjectId + "/exams/" + exam.id, formData) as Observable<ExamTeacher>;
  }

  changeVisibility(subjectId: Number, examId: Number, newVisibility : Boolean) : Observable<any> {

    return this.http.patch(BASE_URL + subjectId + "/exams/" + examId, newVisibility) as Observable<any>;
  }

  importExam(subjectId: number, exam: ImportedExam) : Observable<any> {
    const formData : FormData = new FormData();
    formData.append("name", exam.name);
    formData.append("calificationPercentaje", exam.calificationPercentaje.toString());
    formData.append("visibleExam", exam.visibleExam.toString());
    formData.append("calificationVisible", exam.calificationVisible.toString());
    formData.append("openingDate", exam.openingDate.toString());
    formData.append("closingDate", exam.closingDate.toString());
    formData.append("file", exam.file);
    formData.append("canUploadLate", exam.canUploadLate.toString());
    formData.append("maxTime", exam.maxTime.toString());
    return this.http.post(BASE_URL + subjectId + "/exams/files", formData) as Observable<any>;
  }

}
