import { Router } from '@angular/router';
import { Inject } from '@angular/core';
import { UploadService } from './../../../../services/UploadService';
import { Component } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SubjectEditingDialog } from 'src/app/components/edit-subject/edit-subject.component';

@Component({
  selector: 'app-view-answers-dialog',
  templateUrl: './view-answers-dialog.component.html',
  styleUrls: ['./view-answers-dialog.component.css'],
})
export class ViewAnswersDialogComponent {
  questions: string[] = [];
  answers: string[] = [];
  califications : string[] = [];
  questionCalifications : string[] = [];
  loadingData: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<SubjectEditingDialog>,
    private uploadService: UploadService,
    private router: Router,
    @Inject(MAT_DIALOG_DATA) public data: {subjectId: Number, examId: Number, uploadId: Number}
  ) {
    uploadService.getAnswersAndQuestions(this.data.subjectId, this.data.examId, this.data.uploadId).subscribe((response: any) => {
      this.questions = response.questions;
      this.answers = response.answers;
      this.califications = response.califications;
      this.questionCalifications = response.questionCalifications;
      this.loadingData = false;
    }, (error) => {
      this.router.navigate(['/error']);
    });
  }
}
