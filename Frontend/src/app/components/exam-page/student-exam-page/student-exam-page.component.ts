import { ExamService } from 'src/app/services/ExamService';
import { Component } from '@angular/core';
import { ExamStudent } from 'src/app/models/ExamStudent';
import { ActivatedRoute, Router } from '@angular/router';
import { UploadService } from 'src/app/services/UploadService';
import { ExerciseUpload } from 'src/app/models/ExerciseUpload';
import { ConfirmDialog } from '../../dialogs/ConfirmDialog';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ViewAnswersDialogComponent } from './view-answers-dialog/view-answers-dialog.component';

@Component({
  selector: 'app-student-exam-page',
  templateUrl: './student-exam-page.component.html',
  styleUrls: ['./student-exam-page.component.css'],
})
export class StudentExamPageComponent {
  exam: ExamStudent = new ExamStudent();
  examId: number = 0;
  subjectId: number = 0;
  loadingExam: boolean = true;
  loadingUpload: boolean = false;
  loadingExamFile: boolean = false;

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
    this.loadExam();
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, "Aceptar", {
      horizontalPosition: "center",
      verticalPosition: "top",
      duration: 5000
    });
  }

  loadExam(){
    this.loadingExam = true;
    this.examService.getExam(this.subjectId, this.examId).subscribe(
      (exam: ExamStudent) => {
        this.exam = exam;
        this.exam.openingDate = new Date(exam.openingDate);
        this.exam.closingDate = new Date(exam.closingDate);
        if (exam.exerciseUpload != null) {
          this.exam.exerciseUpload.uploadDate = new Date(
            exam.exerciseUpload.uploadDate
          );
        }
        this.loadingExam = false;
      },
      (_) => {
        this.router.navigate(['/error']);
      }
    );
  }

  goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }

  getOpeningDate(): string {
    return (
      this.exam.openingDate.getDate().toString().padStart(2, '0') +
      '/' +
      (this.exam.openingDate.getMonth() + 1).toString().padStart(2, '0') +
      '/' +
      this.exam.openingDate.getFullYear().toString() +
      ', ' +
      this.exam.openingDate.getHours().toString().padStart(2, '0') +
      ':' +
      this.exam.openingDate.getMinutes().toString().padStart(2, '0') +
      '.'
    );
  }

  getClo(): string {
    return (
      this.exam.openingDate.getDate().toString().padStart(2, '0') +
      '/' +
      (this.exam.openingDate.getMonth() + 1).toString().padStart(2, '0') +
      '/' +
      this.exam.openingDate.getFullYear().toString() +
      ', ' +
      this.exam.openingDate.getHours().toString().padStart(2, '0') +
      ':' +
      this.exam.openingDate.getMinutes().toString().padStart(2, '0') +
      '.'
    );
  }

  calculateDifference(intialDate: Date, endDate: Date): string {
    let difference = intialDate.getTime() - endDate.getTime();
    difference = Math.abs(difference);
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
      return ` ${days} días y ${hours} horas`;
    } else if (hours > 0) {
      return ` ${hours} horas y ${minutes} minutos`;
    } else if (minutes > 0) {
      return ` ${minutes} minutos y ${seconds} segundos`;
    } else {
      return ` ${seconds} segundos`;
    }
  }

  getRemainingTime(): string {
    let timeDiference = '';
    if (this.exam.exerciseUpload != undefined) {
      timeDiference = this.calculateDifference(
        this.exam.exerciseUpload.uploadDate,
        this.exam.closingDate
      );
      if (
        this.exam.exerciseUpload.uploadDate.getTime() <
        this.exam.closingDate.getTime()
      ) {
        return `La tarea se ha entregado ${timeDiference} antes de que se cierre la tarea.`;
      } else {
        return `La tarea se ha entregado ${timeDiference} después de que se cierre la tarea.`;
      }
    } else {
      timeDiference = this.calculateDifference(
        new Date(Date.now()),
        this.exam.closingDate
      );
      if (Date.now() < this.exam.closingDate.getTime()) {
        return `Quedan ${timeDiference} para que se cierre la tarea.`;
      } else {
        return `La tarea se ha atrasado por ${timeDiference}.`;
      }
    }
  }

  getUploadingDate(): string {
    if (this.exam.exerciseUpload != undefined) {
      return "Fecha de entrega: " + this.getDate(this.exam.exerciseUpload.uploadDate);

    } else {
      return 'Todavía no se ha entregado el examen.';
    }
  }

  getDate(date: Date): string {
    return (
      date.getDate().toString().padStart(2, '0') +
      '/' +
      (date.getMonth() + 1).toString().padStart(2, '0') +
      '/' +
      date.getFullYear().toString() +
      ', ' +
      date.getHours().toString().padStart(2, '0') +
      ':' +
      date.getMinutes().toString().padStart(2, '0') +
      '.'
    );
  }

  getClosingDate(): string {
    return (
      this.exam.closingDate.getDate().toString().padStart(2, '0') +
      '/' +
      (this.exam.closingDate.getMonth() + 1).toString().padStart(2, '0') +
      '/' +
      this.exam.closingDate.getFullYear().toString() +
      ', ' +
      this.exam.closingDate.getHours().toString().padStart(2, '0') +
      ':' +
      this.exam.closingDate.getMinutes().toString().padStart(2, '0') +
      '.'
    );
  }

  onFileSelected(event: any){
    const file:File = event.target.files[0];
    if (file){
      this.openDialog(file)
    }
  }

  openDialog(file: File): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Seguro que quieres subir el fichero ${file.name}?`},
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result : any) => {
      if (result) {
        this.uploadFile(file);
      }
    });
  }

  uploadFile(file: File){
    this.loadingUpload = true;
    this.uploadService.uploadExamFile(this.subjectId, this.examId, file).subscribe(
      (response) => {
        this.loadExam();
        this.loadingUpload = false;
      },
      (error) => {
        this.loadingUpload = false;
        this.openSnackBar('Error al subir el fichero intentalo de nuevo más tarde.');
      }
    );
  }

  downloadExam(){
    this.uploadService.downloadExam(this.subjectId, this.examId, this.exam.exerciseUpload.id).subscribe(
      (response) => {
        this.uploadService.donwloadFile(response);
      },
      (error) => {
        this.openSnackBar('Error al descargar el fichero intentalo de nuevo más tarde.');
      }
    );
  }

  uploadedExam(){
    return this.exam.exerciseUpload != undefined;
  }

  toQuestionsExam(){
    this.router.navigate([`/subject/${this.subjectId}/exam/${this.examId}/resolveExam`]);
  }

  openDialogView(): void {
    this.dialog.open(ViewAnswersDialogComponent, {
      data: { subjectId: this.subjectId, examId: this.examId, uploadId: this.exam.exerciseUpload.id },
      width: '90%',
      height: '90%',
    });
  }

  canUpload() : boolean{
    return this.exam.openingDate.getTime() < Date.now();
  }

  downloadExamFile(){
    this.loadingExamFile = true;
    this.examService.getFiles(this.subjectId, this.examId).subscribe(
      (response) => {
        this.uploadService.donwloadFile(response);
        this.loadingExamFile = false;
      },
      (_) => {
        this.openSnackBar(
          'Error al descargar el fichero intentalo de nuevo más tarde.'
        );
        this.loadingExamFile = false;
      }
    );
  }

  dateCondition() : boolean{
    return this.exam.closingDate.getTime() > Date.now();
  }

}
