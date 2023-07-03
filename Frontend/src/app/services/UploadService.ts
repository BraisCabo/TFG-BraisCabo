import { Observable } from 'rxjs';
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CalificationFile } from '../models/CalificationFile';
import { CalificationQuestions } from '../models/CalificationQuestions';

const BASE_URL = '/api/subjects/';

@Injectable({ providedIn: 'root' })
export class UploadService {

  constructor(private http: HttpClient) { }

  downloadAll(subectId: Number, examId : Number) : Observable<any>{
    return this.http.get(BASE_URL + subectId + '/exams/' + examId + '/uploads/files', { responseType: 'blob', observe: 'response' })
  }

  uploadExamFile(subjectId: number, examId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('newFile', file);
    return this.http.post(BASE_URL + `${subjectId}/exams/${examId}/uploads/files`, formData);
  }

  uploadExamQuestions(subjectId: Number, examId: Number, answers: string[]): Observable<any> {
    const formData = {
      answers: answers
    }
    return this.http.post(BASE_URL + `${subjectId}/exams/${examId}/uploads/questions`, answers);
  }

  downloadExam(subjectId: number, examId: number, uploadId : number): Observable<any> {
    return this.http.get(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}/files`, { responseType: 'blob', observe: 'response' });
  }

  getFileNameFromResponse(response: HttpResponse<Blob>): string {
    const contentDispositionHeader = response.headers.get('content-disposition');
    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    const matches = filenameRegex.exec(contentDispositionHeader??"");
    let filename = 'file';

    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '');
    }

    return filename;
  }

  public donwloadFile(response : any){
      const blob = new Blob([response.body]);
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      const filename = this.getFileNameFromResponse(response);
      link.download = filename
      link.click();
      link.remove();
  }

  public getAnswersAndQuestions(subjectId: Number, examId : Number, uploadId : Number ) : Observable<any>{
    return this.http.get(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}/questions`) as Observable<any>;
  }

  public findAll(subjectId: Number, examId : Number, name : String) : Observable<any>{
    return this.http.get(BASE_URL + `${subjectId}/exams/${examId}/uploads/?name=${name}`) as Observable<any>;
  }

  public delete(subjectId: Number, examId : Number, uploadId : Number) : Observable<any>{
    return this.http.delete(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}`) as Observable<any>;
  }

  public uploadCalification(subjectId: Number, examId : Number, uploadId : Number, calification : CalificationFile) : Observable<any>{
    return this.http.post(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}/califications/files`, calification) as Observable<any>;
  }

  public editCalification(subjectId: Number, examId : Number, uploadId : Number, calification : CalificationFile) : Observable<any>{
    return this.http.put(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}/califications/files`, calification) as Observable<any>;
  }

  public uploadCalificationQuestions(subjectId: Number, examId : Number, uploadId : Number, calification : CalificationQuestions) : Observable<any>{
    return this.http.post(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}/califications/questions`, calification) as Observable<any>;
  }

  public editCalificationQuestions(subjectId: Number, examId : Number, uploadId : Number, calification : CalificationQuestions) : Observable<any>{
    return this.http.put(BASE_URL + `${subjectId}/exams/${examId}/uploads/${uploadId}/califications/questions`, calification) as Observable<any>;
  }
}
