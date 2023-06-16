import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Exam } from 'src/app/models/Exam';
import { ExamService } from 'src/app/services/ExamService';

@Component({
  selector: 'app-teacher-exam-page',
  templateUrl: './teacher-exam-page.component.html',
  styleUrls: ['./teacher-exam-page.component.css']
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
      this.exam.openingDate.getMonth().toString().padStart(2, '0') +
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
    let remainingTime = this.exam.closingDate.getTime() - Date.now();
    if (remainingTime < 0) {
      return '00:00:00';
    }
    let hours = Math.floor(remainingTime / 3600000);
    remainingTime -= hours * 3600000;
    let minutes = Math.floor(remainingTime / 60000);
    remainingTime -= minutes * 60000;
    let seconds = Math.floor(remainingTime / 1000);
    return (
      hours.toString().padStart(2, '0') +
      ':' +
      minutes.toString().padStart(2, '0') +
      ':' +
      seconds.toString().padStart(2, '0')
    );
  }


  getClosingDate(): string {
    return (
      this.exam.closingDate.getDate().toString().padStart(2, '0') +
      '/' +
      this.exam.closingDate.getMonth().toString().padStart(2, '0') +
      '/' +
      this.exam.closingDate.getFullYear().toString() +
      ', ' +
      this.exam.closingDate.getHours().toString().padStart(2, '0') +
      ':' +
      this.exam.closingDate.getMinutes().toString().padStart(2, '0') +
      '.'
    );
  }
}
