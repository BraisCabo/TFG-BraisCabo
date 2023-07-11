import { Component, Inject } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/AuthService';
import { ExamService } from 'src/app/services/ExamService';
import { SubjectService } from 'src/app/services/SubjectService';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { ImportedExam } from 'src/app/models/ImportedExam';

export function dateValidator(openingDate: any): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    let closingDate = control;
    try {
      if (
        openingDate.value &&
        closingDate.value &&
        closingDate.value.getTime() <= openingDate.value.getTime()
      ) {
        return { incorrectDate: true };
      }
      return null;
    } catch (error) {
      return { incorrectDate: true };
    }
  };
}
@Component({
  selector: 'app-import-exam',
  templateUrl: './import-exam.component.html',
  styleUrls: ['./import-exam.component.css']
})
export class ImportExamComponent {
  name: FormControl = new FormControl('', Validators.required);
  calificationPercentaje: FormControl = new FormControl(0, [
    Validators.required,
    Validators.max(100),
    Validators.min(0),
  ]);
  examFile!: File;
  visibleExam: string = 'false';
  calificationVisible: string = 'false';
  openingDate: FormControl = new FormControl(new Date(), Validators.required);
  closingDate: FormControl = new FormControl(new Date(), [
    Validators.required,
    dateValidator(this.openingDate) as any,
  ]);
  subjectId!: number;
  loading: boolean = false;
  canUploadLate : string = 'false';
  maxTime : FormControl = new FormControl(1,[Validators.required, Validators.min(1)]);

  constructor(
    private _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) private _locale: string,
    private router: Router,
    private subjectService: SubjectService,
    private activatedRoute: ActivatedRoute,
    private examService: ExamService,
    private authService: AuthService,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
  ) {
    this.subjectId = Number(this.activatedRoute.snapshot.paramMap.get('id'));
    subjectService.getSubjectById(this.subjectId).subscribe(
      (_) => {},
      (_) => {
        this.router.navigate(['/error']);
      }
    );
    this.authService.currentUserObserver().subscribe((user) => {
      if (user) {
        this.subjectService
          .isSubjectTeacher(authService.getCurrentUser().id, this.subjectId)
          .subscribe((isTeacher) => {
            if (!isTeacher){
              router.navigate(['/error']);
            }
          });
      }else{
        router.navigate(['/error']);
      }
    });
    let date = new Date();

    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    this._locale = 'es';
    this._adapter.setLocale(this._locale);
    this.openingDate.setValue(date);
    this.closingDate.setValue(date);
    this.closingDate.markAsTouched();
    this.openingDate.valueChanges.subscribe((_) => {
      this.closingDate.updateValueAndValidity();
    });
  }

  onFileSelected(event: any){
    this.examFile = event.target.files[0];
  }

  getNameError() {
    return 'El nombre es obligatorio';
  }

  openCreateExamDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Deseas crear el examen ${this.name.value}?`},
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createExam();
      }
    });
  }

  getCalificationPercentajeError() {
    if (this.calificationPercentaje.hasError('required')) {
      return 'Este campo es obligatorio';
    }
    if (this.calificationPercentaje.hasError('max')) {
      return 'No puede ser mayor que 100';
    }
    return 'No puede ser menor que 0';
  }

  changedOpeningTime(event: any) {
    let date: Date = new Date(this.openingDate.value);
    date.setHours(event.split(':')[0]);
    date.setMinutes(event.split(':')[1]);
    this.openingDate.setValue(date);
  }

  changedClosingTime(event: any) {
    let date: Date = new Date(this.closingDate.value);
    date.setHours(event.split(':')[0]);
    date.setMinutes(event.split(':')[1]);
    this.closingDate.setValue(date);
  }

  getOpeningDateError() {
    return 'Fecha no válida';
  }

  createExam() {
    this.loading = true;
    const importedExam: ImportedExam = {
      name: this.name.value,
      calificationPercentaje: this.calificationPercentaje.value,
      openingDate: this.openingDate.value,
      closingDate: this.closingDate.value,
      visibleExam: this.visibleExam === 'true',
      calificationVisible: this.calificationVisible === 'true',
      file: this.examFile,
      canUploadLate : this.canUploadLate === 'true',
      maxTime : this.maxTime.value.toString()
    };
    this.examService.importExam(this.subjectId, importedExam).subscribe(
      (_) => {
        this.router.navigate(['/subject/' + this.subjectId]);
      },
      (_) => {
        this.openSnackBar(
          "No se ha podido importar el examen por que ya existe otro con ese nombre o el fichero no es válido"
        );
        this.loading = false;
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

  isValidExam() {
    return (
      this.name.valid &&
      this.calificationPercentaje.valid &&
      this.openingDate.valid &&
      this.closingDate.valid &&
      this.examFile
    );
  }

  goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }

  deleteFile(){
    this.examFile = undefined as any;
  }
}
