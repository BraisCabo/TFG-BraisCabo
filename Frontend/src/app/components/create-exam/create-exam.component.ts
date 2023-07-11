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
import { ExamChanges } from 'src/app/models/ExamChanges';

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
  selector: 'app-create-exam',
  templateUrl: './create-exam.component.html',
  styleUrls: ['./create-exam.component.css'],
})
export class CreateExamComponent {
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
  type: FormControl = new FormControl('UPLOAD', Validators.required);
  questions: string[] = [];
  date: Date = new Date();
  subjectId!: number;
  loading: boolean = false;
  canRepeat : string = 'false';
  canUploadLate : string = 'false';
  questionsCalification: Number[] = [];
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
    this.date.setHours(0);
    this.date.setMinutes(0);
    this.date.setSeconds(0);
    this._locale = 'es';
    this._adapter.setLocale(this._locale);
    this.openingDate.setValue(this.date);
    this.closingDate.setValue(this.date);
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
    console.log(date)
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

  invalidQuestions() {
    return this.questions.length == 0 && this.type.value === 'QUESTIONS';
  }

  newQuestion() {
    this.questions.push('');
    this.questionsCalification.push(0);
  }

  deleteQuestion(index: number) {
    this.questions.splice(index, 1);
    this.questionsCalification.splice(index, 1);
  }

  trackByFn(index: number, item: any): any {
    return index;
  }

  createExam() {
    this.loading = true;
    const examDTO: ExamChanges = {
      id: null as any,
      name: this.name.value,
      calificationPercentaje: this.calificationPercentaje.value,
      openingDate: this.openingDate.value,
      closingDate: this.closingDate.value,
      type: this.type.value,
      questions: this.questions,
      visibleExam: this.visibleExam === 'true',
      calificationVisible: this.calificationVisible === 'true',
      examFile: this.examFile,
      deletedFile : false,
      canRepeat : this.canRepeat === 'true',
      canUploadLate : this.canUploadLate === 'true',
      questionsCalifications: this.questionsCalification.map((calification) => {
        return calification.toString();
      }),
      maxTime : this.maxTime.value.toString()
    };
    this.examService.createExam(this.subjectId, examDTO, this.examFile).subscribe(
      (_) => {
        this.router.navigate(['/subject/' + this.subjectId]);
      },
      (_) => {
        this.openSnackBar(
          "No se ha podido crear el examen por que ya existe otro con ese nombre"
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
      this.type.valid &&
      !this.invalidQuestions() &&
      (this.maxTime.valid || this.type.value === 'UPLOAD')
    );
  }

  goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }

  deleteFile(){
    this.examFile = undefined as any;
  }
}
