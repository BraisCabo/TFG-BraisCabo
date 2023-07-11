import { MatDialog } from '@angular/material/dialog';
import { UploadService } from './../../../services/UploadService';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamTeacher } from 'src/app/models/ExamTeacher';
import { ExamService } from 'src/app/services/ExamService';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ExerciseUpload } from 'src/app/models/ExerciseUpload';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-teacher-exam-page',
  templateUrl: './teacher-exam-page.component.html',
  styleUrls: ['./teacher-exam-page.component.css'],
})
export class TeacherExamPageComponent {
  loadingExam: boolean = true;
  examId: number = 0;
  subjectId: number = 0;
  exam!: ExamTeacher;
  loadingDownload = false;
  name = '';
  exerciseUploads: ExerciseUpload[] = [];
  loadingExamFile = false;
  loadingExport = false;

  constructor(
    private examService: ExamService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private uploadService: UploadService,
    private _snackBar: MatSnackBar
  ) {
    this.examId = Number(this.activatedRoute.snapshot.paramMap.get('examId'));
    this.subjectId = Number(
      this.activatedRoute.snapshot.paramMap.get('subjectId')
    );
    this.examService.getExam(this.subjectId, this.examId).subscribe(
      (exam) => {
        this.exam = exam;
        this.exam.openingDate = new Date(this.exam.openingDate);
        this.exam.closingDate = new Date(this.exam.closingDate);
        this.loadingExam = false;
      },
      (_) => {
        router.navigate(['/error']);
      }
    );
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

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Aceptar', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 5000,
    });
  }

  getRemainingTime(): string {
    let difference = this.exam.closingDate.getTime() - Date.now();
    let message = difference < 0 ? 'Se ha cerrado hace' : 'Se cerrará en';
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
      return `${message} ${days} días y ${hours} horas.`;
    } else if (hours > 0) {
      return `${message} ${hours} horas y ${minutes} minutos.`;
    } else if (minutes > 0) {
      return `${message} ${minutes} minutos y ${seconds} segundos.`;
    } else {
      return `${message} ${seconds} segundos.`;
    }
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

  getExamVisibility(): string {
    if (this.exam.visibleExam) {
      return 'El examen es visible por los alumnos.';
    } else {
      return 'El examen no es visible por los alumnos.';
    }
  }

  getCalificationVisibility(): string {
    if (this.exam.calificationVisible) {
      return 'Los alumnos pueden ver la calificación del examen.';
    } else {
      return 'Los alumnos no pueden ver la calificación del examen.';
    }
  }

  goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }

  editExam() {
    this.router.navigate([
      '/subject/' + this.subjectId + '/exam/' + this.examId + '/editExam',
    ]);
  }

  getNumberOfUploadsMessage(): string {
    if (this.exam.exerciseUploads == 0) {
      return 'Todavía no hay ninguna entrega.';
    }
    let message = this.exam.exerciseUploads == 1 ? '' : 's';
    return (
      'La tarea ha sido entregada por ' +
      this.exam.exerciseUploads +
      ' alumno' +
      message +
      '.'
    );
  }

  downloadAllUploads() {
    this.download(this.loadingDownload, this.uploadService.downloadAll(this.subjectId, this.examId));
  }

  private download(
    loadingVarible: boolean,
    resource : Observable<any>
  ) {
    loadingVarible = true;
    resource.subscribe(
      (response) => {
        this.uploadService.donwloadFile(response);
        loadingVarible = false;
      },
      (_) => {
        this.openSnackBar(
          'Error al descargar el fichero intentalo de nuevo más tarde.'
        );
        loadingVarible = false;
      }
    );
  }

  exportExam() {
    this.download(this.loadingExport, this.examService.exportExam(this.subjectId, this.examId));
  }

  goToCalificate() {
    this.router.navigate([
      '/subject/' + this.subjectId + '/exam/' + this.examId + '/calificate',
    ]);
  }

  downloadExam() {
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

  getRepeatExamMessage(): string {
    if (this.exam.canRepeat) {
      return 'Los alumnos pueden repetir el examen.';
    } else {
      return 'Los alumnos no pueden repetir el examen.';
    }
  }

  getLateUploadMessage(): string {
    if (this.exam.canUploadLate) {
      return 'Los alumnos pueden entregar el examen fuera de plazo.';
    } else {
      return 'Los alumnos no pueden entregar el examen fuera de plazo.';
    }
  }
}
