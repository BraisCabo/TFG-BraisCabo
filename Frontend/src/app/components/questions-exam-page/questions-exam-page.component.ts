import { UploadService } from 'src/app/services/UploadService';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from 'src/app/services/ExamService';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { QuestionsDTO } from 'src/app/models/QuestionsDTO';

@Component({
  selector: 'app-questions-exam-page',
  templateUrl: './questions-exam-page.component.html',
  styleUrls: ['./questions-exam-page.component.css'],
})
export class QuestionsExamPageComponent {
  questions!: QuestionsDTO;
  answers: string[] = [];
  subjectId: Number = 0;
  examId: Number = 0;
  loadingQuestions = true;
  loadingSend = false;

  constructor(
    private examService: ExamService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private uploadService: UploadService,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
  ) {
    this.examId = Number(this.activatedRoute.snapshot.paramMap.get('examId'));
    this.subjectId = Number(
      this.activatedRoute.snapshot.paramMap.get('subjectId')
    );
    this.examService.getQuestions(this.subjectId, this.examId).subscribe(
      (questions) => {
        this.questions = questions;
        this.questions.questions.forEach((question) => {
          this.answers.push('');
        }
        );
        this.loadingQuestions = false;
      },
      () => {
        router.navigate(['/error']);
      }
    );
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, "Aceptar", {
      horizontalPosition: "center",
      verticalPosition: "top",
      duration: 5000
    });
  }

  sendAnswers(){
    this.loadingSend = true;
    this.uploadService.uploadExamQuestions(this.subjectId, this.examId, this.answers).subscribe(
      () => {
        this.loadingSend = false;
        this.router.navigate(['/subject/'+this.subjectId+'/exam/'+ this.examId]);
      },
      () => {
        this.openSnackBar("Error al subir las preguntas inténtalo de nuevo más tarde");
        this.loadingSend = false;
      }
    );
  }

  openSendDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Deseas subir las preguntas y terminar el examen?`},
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.sendAnswers();
      }
    });
  }


}
