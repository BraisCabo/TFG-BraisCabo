import { LocalStorageService } from './../../services/LocalStorageService';
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
  examData!: QuestionsDTO;
  answers: string[] = [];
  subjectId: Number = 0;
  examId: Number = 0;
  loadingQuestions = true;
  loadingSend = false;
  examKey = "";


  constructor(
    private examService: ExamService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private uploadService: UploadService,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private localStorageService: LocalStorageService
  ) {
    this.examId = Number(this.activatedRoute.snapshot.paramMap.get('examId'));
    this.subjectId = Number(
      this.activatedRoute.snapshot.paramMap.get('subjectId')
    );
    this.examKey = this.subjectId + "-" + this.examId + "-";
    this.examService.getQuestions(this.subjectId, this.examId).subscribe(
      (data) => {
        this.examData = data;
        this.examData.startedDate = new Date(data.startedDate);
        this.examData.closingDate = new Date(data.closingDate);
        console.log(this.examData)
        for (let i = 0; i < this.examData.questions.length; i++) {
          this.answers.push(this.localStorageService.getItem(this.examKey + i));
        }
        this.sendAuto();
        this.loadingQuestions = false;
      },
      () => {
        router.navigate(['/error']);
      }
    );
  }

  sendAuto(): void {
    setTimeout(() => {
      this.sendAnswers();
    }, this.getTimeLeft());
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, "Aceptar", {
      horizontalPosition: "center",
      verticalPosition: "top",
      duration: 5000
    });
  }

  getTimeLeft() : number {
    let timeLimit = (this.examData.startedDate.getTime() + this.examData.maxTime * 60 * 1000) - new Date().getTime()
    if (timeLimit < 0) return 0;
    let timeToClose = Math.abs(new Date().getTime() - this.examData.closingDate.getTime())
    return Math.min(timeLimit, timeToClose) ;
  }


  sendAnswers(){
    this.loadingSend = true;
    this.uploadService.uploadExamQuestions(this.subjectId, this.examId, this.answers).subscribe(
      () => {
        for (let i = 0; i < this.examData.questions.length; i++) {
          this.localStorageService.setItem(this.examKey + i, "");
        }
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

  textChanged(i: number): void {
    this.localStorageService.setItem(this.examKey + i, this.answers[i]);
  }

  getClosingDate(): string{
    let message = 'El examen se cerrará en: ';
    let difference = this.getTimeLeft()
    //this.exam.closingDate.getTime() - this.exam.openingDate.getTime();
    const millisecondsPerSecond = 1000;
    const millisecondsPerMinute = 60 * millisecondsPerSecond;
    const millisecondsPerHour = 60 * millisecondsPerMinute;
    const millisecondsPerDay = 24 * millisecondsPerHour;

    const days = Math.floor(difference / millisecondsPerDay);
    const hours = Math.floor(
      (difference % millisecondsPerDay) / millisecondsPerHour
    );
    const minutes = Math.floor(
      (difference % millisecondsPerHour) / millisecondsPerMinute
    );
    const seconds = Math.floor(
      (difference % millisecondsPerMinute) / millisecondsPerSecond
    );

    if (days > 0) {
      return `${message} ${days} días y ${hours} horas.`;
    } else if (hours > 0) {
      return `${message} ${hours} horas y ${minutes} minutos.`;
    } else if (minutes > 0) {
      return `${message} ${minutes} minutos y ${seconds} segundos.`;
    } else {
      return `${message} ${seconds} segundos.`;
    }
  }


}
