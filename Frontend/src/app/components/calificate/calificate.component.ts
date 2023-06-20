import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { ExerciseUpload } from 'src/app/models/ExerciseUpload';
import { UploadService } from 'src/app/services/UploadService';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { MatDialog } from '@angular/material/dialog';
import { SetCalificationDialogComponent } from './set-calification-dialog/set-calification-dialog.component';
import { ViewAnswersDialogComponent } from '../exam-page/student-exam-page/view-answers-dialog/view-answers-dialog.component';
import { EditCalificationDialogComponent } from './edit-calification-dialog/edit-calification-dialog.component';

@Component({
  selector: 'app-calificate',
  templateUrl: './calificate.component.html',
  styleUrls: ['./calificate.component.css'],
})
export class CalificateComponent {
  exerciseUploads: ExerciseUpload[] = [];
  examId: number = 0;
  subjectId: number = 0;
  name = '';
  loadingCalifications = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private uploadService: UploadService,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) {
    this.examId = Number(this.activatedRoute.snapshot.paramMap.get('examId'));
    this.subjectId = Number(
      this.activatedRoute.snapshot.paramMap.get('subjectId')
    );
    this.searchUploads()
  }

  searchUploads(){
    this.loadingCalifications = true;
    this.uploadService
    .findAll(this.subjectId, this.examId, this.name)
    .subscribe(
      (uploads) => {
        this.exerciseUploads = uploads;
        this.exerciseUploads.forEach((upload) => {
          upload.uploadDate = new Date(upload.uploadDate);
          upload.exam.closingDate = new Date(upload.exam.closingDate);
          upload.exam.openingDate = new Date(upload.exam.openingDate);
        });
        this.loadingCalifications = false;
      },
      (_) => {
        this.router.navigate(['/error']);
      }
    );
  }

  getTimeMessage(upload: ExerciseUpload) : string{
    if (!upload.uploaded){
      return "No entregado";
    }
    if (upload.uploadDate > upload.exam.closingDate){
      return `Se ha entregado tarde, ${this.getDate(upload.uploadDate)}`;
    }else{
      return `Se ha entregado a tiempo, ${this.getDate(upload.uploadDate)}`;
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

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Aceptar', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 5000,
    });
  }

  getUpload(upload: ExerciseUpload) {
    this.uploadService
      .downloadExam(this.subjectId, this.examId, upload.id)
      .subscribe(
        (response) => {
          this.uploadService.donwloadFile(response);
        },
        (_) => {
          this.openSnackBar('Error al descargar el archivo');
        }
      );
  }

  deleteUpload(upload: ExerciseUpload){
    this.uploadService.delete(this.subjectId, this.examId, upload.id).subscribe(
      (_) => {
        this.searchUploads()
      },
      (_) => {
        this.openSnackBar('Error al eliminar la entrega');
      }
    );
  }

  openDialog(upload: ExerciseUpload): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Deseas borrar la entrega de ${upload.student.name} ${upload.student.lastName}?`},
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result : any) => {
      if (result) {
        this.deleteUpload(upload)
      }
    });
  }

  calificate(upload: ExerciseUpload, i : number) : void{
    let dialogRef= this.dialog.open(SetCalificationDialogComponent, {
      data: { subjectId: this.subjectId, examId: this.examId, uploadId: upload.id},
      width: '90%',
      height: '90%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.exerciseUploads[i].calification = result.calification;
        this.exerciseUploads[i].comment = result.comment;
      }
    });
  }

  editCalificate(upload: ExerciseUpload, i : number) : void{
    let dialogRef= this.dialog.open(EditCalificationDialogComponent, {
      data: { subjectId: this.subjectId, examId: this.examId, uploadId: upload.id, calification: upload.calification, comment: upload.comment},
      width: '90%',
      height: '90%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.exerciseUploads[i].calification = result.calification;
        this.exerciseUploads[i].comment = result.comment;
      }
    });
  }

  viewAnswers(upload: ExerciseUpload) : void{
    this.dialog.open(ViewAnswersDialogComponent, {
      data: { subjectId: this.subjectId, examId: this.examId, uploadId: upload.id},
      width: '90%',
      height: '90%',
    });
  }

  goToExam(){
    this.router.navigate([`/subject/${this.subjectId}/exam/${this.examId}`]);
  }

}
