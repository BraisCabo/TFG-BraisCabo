import { Component, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UploadService } from 'src/app/services/UploadService';
import { ConfirmDialog } from '../../dialogs/ConfirmDialog';
import { CalificationFile } from 'src/app/models/CalificationFile';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalificationQuestions } from 'src/app/models/CalificationQuestions';


@Component({
  selector: 'app-edit-calification-dialog-questions',
  templateUrl: './edit-calification-dialog-questions.component.html',
  styleUrls: ['./edit-calification-dialog-questions.component.css']
})
export class EditCalificationDialogQuestionsComponent {
  subjectId: Number = 0;
  examId: Number = 0;
  uploadId: Number = 0;
  califications: Number[] = [];
  comment: String = '';
  loading : boolean = false;
  questionsCalification : String[] = [];

  constructor(
    public dialogRef: MatDialogRef<EditCalificationDialogQuestionsComponent>,
    private uploadService: UploadService,
    private router: Router,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA)
    public data: { subjectId: Number; examId: Number; uploadId: Number, questionsCalifications: String[], califications: String[], comment: String }
  ) {
    this.subjectId = data.subjectId;
    this.examId = data.examId;
    this.uploadId = data.uploadId;
    this.califications = data.califications.map(calification => Number(calification));
    this.comment = data.comment;
    this.questionsCalification = data.questionsCalifications;
  }

  getErrorMessage() {
    return 'La calificación de todas las preguntas debe ser mayor a 0 y menor que la puntuación de esa pregunta';
  }

  isValid(){
    for(let i = 0; i < this.califications.length; i++){
      if(Number(this.califications[i]) < 0 || Number(this.califications[i]) > Number(this.questionsCalification[i])){
        return false;
      }
    }
    return true;
  }

  isValidCalification(index: number){
    if(Number(this.califications[index]) < 0 || Number(this.califications[index]) > Number(this.questionsCalification[index])){
      return false;
    }
    return true;
  }

  openDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Deseas establecer esta calificación?` },
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.setCalification();
      }
    });
  }

  setCalification() {
    this.loading = true;
    let studentCalification: CalificationQuestions = new CalificationQuestions();
    studentCalification.comment = this.comment;
    studentCalification.questionsCalification = this.califications.map((calification) => {
      if (calification === null) {
        return "0";
      }
      return calification.toString();
    });


    this.uploadService
      .editCalificationQuestions(
        this.subjectId,
        this.examId,
        this.uploadId,
        studentCalification
      )
      .subscribe(
        (response) => {
          this.dialogRef.close(response);
        },
        (_) => {
          this.openSnackBar(
            'Error al establecer la calificación inténtalo de nuevo'
          );
          this.loading = false;
        }
      );
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Aceptar', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 5000,
    });
  }

  closeDialog() {
    this.dialogRef.close();
  }
}
