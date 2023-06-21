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
import { Calification } from 'src/app/models/Calification';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-edit-calification-dialog',
  templateUrl: './edit-calification-dialog.component.html',
  styleUrls: ['./edit-calification-dialog.component.css'],
})
export class EditCalificationDialogComponent {
  subjectId: Number = 0;
  examId: Number = 0;
  uploadId: Number = 0;
  calification: FormControl = new FormControl(0, [
    Validators.required,
    Validators.min(0),
    Validators.max(10),
  ]);
  comment: String = '';
  loading : boolean = false;

  constructor(
    public dialogRef: MatDialogRef<EditCalificationDialogComponent>,
    private uploadService: UploadService,
    private router: Router,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA)
    public data: { subjectId: Number; examId: Number; uploadId: Number, calification: String, comment: String}
  ) {
    this.subjectId = data.subjectId;
    this.examId = data.examId;
    this.uploadId = data.uploadId;
    this.calification.setValue(data.calification);
    this.comment = data.comment;
  }

  getErrorMessage() {
    if (this.calification.hasError('required')) {
      return 'Debe ingresar una calificación';
    }
    if (this.calification.hasError('min')) {
      return 'La nota mínima es 0';
    }
    if (this.calification.hasError('max')) {
      return 'La nota máxima es 10';
    }
    return '';
  }

  openDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Deseas cambiar esta calificación?` },
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
    let studentCalification: Calification = new Calification();
    studentCalification.calification = this.calification.value;
    studentCalification.comment = this.comment;

    this.uploadService
      .editCalification(
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
