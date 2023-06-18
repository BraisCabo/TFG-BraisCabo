import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Exam } from 'src/app/models/Exam';
import { ExamService } from 'src/app/services/ExamService';

@Component({
  selector: 'app-teacher-exam-page',
  templateUrl: './teacher-exam-page.component.html',
  styleUrls: ['./teacher-exam-page.component.css'],
})
export class TeacherExamPageComponent {
  loadingExam: boolean = true;
  examId: number = 0;
  subjectId: number = 0;
  exam!: Exam;

  constructor(
    private examService: ExamService,
    private activatedRoute: ActivatedRoute,
    private router: Router
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

  getRemainingTime(): string {
    let difference = this.exam.closingDate.getTime() - Date.now()
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

  getExamVisibility() : string{
    if (this.exam.visibleExam){
      return "El examen es visible por los alumnos.";
    }else{
      return "El examen no es visible por los alumnos."
    }
  }

  getCalificationVisibility() : string {
    if(this.exam.calificationVisible){
      return "Los alumnos pueden ver la calificación del examen."
    }else {
      return "Los alumnos no pueden ver la calificación del examen."
    }
  }

  goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }

  editExam(){
    console.log('/subjects/'+ this.subjectId + "/exams/"+ this.examId + "/editExam")
    this.router.navigate(['/subject/'+ this.subjectId + "/exam/"+ this.examId + "/editExam"])
  }
}
