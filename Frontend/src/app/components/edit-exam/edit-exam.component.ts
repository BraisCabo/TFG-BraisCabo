import { Component, Inject } from '@angular/core';
import {
  FormControl,
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
import { dateValidator } from '../create-exam/create-exam.component';
import { ExamTeacher } from 'src/app/models/ExamTeacher';
import { ExamChanges } from 'src/app/models/ExamChanges';


@Component({
  selector: 'app-edit-exam',
  templateUrl: './edit-exam.component.html',
  styleUrls: ['./edit-exam.component.css']
})
export class EditExamComponent {
  name: FormControl = new FormControl('', Validators.required);
  calificationPercentaje: FormControl = new FormControl(0, [
    Validators.required,
    Validators.max(100),
    Validators.min(0),
  ]);
  visibleExam: string = 'false';
  calificationVisible: string = 'false';
  openingDate: FormControl = new FormControl(Date, Validators.required);
  closingDate: FormControl = new FormControl(Date, [
    Validators.required,
    dateValidator(this.openingDate) as any,
  ]);
  type: FormControl = new FormControl('UPLOAD', Validators.required);
  questions: string[] = [];
  date: Date = new Date();
  subjectId!: number;
  examId!: number;
  loading: boolean = false;
  loadingExam : boolean = true;
  examFile!: File;
  deletedFile : boolean = false;
  examFileName : string = "";
  canRepeat : string = 'false';
  canUploadLate : string = 'false';

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
    this.subjectId = Number(this.activatedRoute.snapshot.paramMap.get('subjectId'));
    this.examId = Number(this.activatedRoute.snapshot.paramMap.get('examId'))
    this.examService.getExam(this.subjectId, this.examId).subscribe(
      (exam) => {
        this.name.setValue(exam.name)
        this.calificationPercentaje.setValue(exam.calificationPercentaje)
        this.visibleExam = exam.visibleExam ? 'true' : 'false'
        this.calificationVisible = exam.calificationVisible ? 'true' : 'false'

        this.openingDate.setValue(new Date(exam.openingDate))
        this.closingDate.setValue(new Date(exam.closingDate))
        this.examFileName = exam.examFile

        this.type.setValue(exam.type)
        this.questions = exam.questions
        this.canRepeat = exam.canRepeat ? 'true' : 'false'
        this.canUploadLate = exam.canUploadLate ? 'true' : 'false'
        this.loadingExam = false;
        console.log(this.loadingExam)
      },
      (_) => {
        router.navigate(['/error']);
      }
    );
    this._locale = 'es';
    this._adapter.setLocale(this._locale);

    this.closingDate.markAsTouched();
    this.openingDate.valueChanges.subscribe((_) => {
      this.closingDate.updateValueAndValidity();
    });
  }

  getNameError() {
    return 'El nombre es obligatorio';
  }

  openEditExamDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Deseas editar el examen ${this.name.value}?`},
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.editExam();
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

  invalidQuestions() {
    return this.questions.length == 0 && this.type.value === 'QUESTIONS';
  }

  newQuestion() {
    this.questions.push('');
  }

  deleteQuestion(index: number) {
    this.questions.splice(index, 1);
  }

  trackByFn(index: number, item: any): any {
    return index;
  }

  editExam() {
    this.loading = true;
    const examDTO: ExamChanges = {
      id : this.examId,
      name: this.name.value,
      calificationPercentaje: this.calificationPercentaje.value,
      openingDate: this.openingDate.value,
      closingDate: this.closingDate.value,
      type: this.type.value,
      questions: this.questions,
      visibleExam: this.visibleExam === 'true',
      calificationVisible: this.calificationVisible === 'true',
      examFile: this.examFile,
      deletedFile: this.deletedFile,
      canRepeat: this.canRepeat === 'true',
      canUploadLate: this.canUploadLate === 'true'
    };
    this.examService.updateExam(this.subjectId, examDTO).subscribe(
      (_) => {
        this.goToExam()
      },
      (_) => {
        this.openSnackBar(
          "No se ha podido editar el examen por que ya existe otro con ese nombre"
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
      !this.invalidQuestions()
    );
  }

  getTime(date: Date): string{
    const minutes = date.getMinutes().toString().padStart(2, '0')
    const hour = date.getHours().toString().padStart(2, '0')
    return hour + ":" + minutes
  }

  goToExam() {
    this.router.navigate(['/subject/'+this.subjectId+'/exam/'+this.examId]);
  }

  deleteFile(){
    this.deletedFile = true;
    this.examFileName = "";
    this.examFile = undefined as any;
  }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.examFile = event.target.files[0];
      this.examFileName = this.examFile.name;
    }
  }
}
