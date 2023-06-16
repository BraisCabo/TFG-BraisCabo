import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Exam } from 'src/app/models/Exam';
import { AuthService } from 'src/app/services/AuthService';
import { ExamService } from 'src/app/services/ExamService';
import { SubjectService } from 'src/app/services/SubjectService';

@Component({
  selector: 'app-exam-page',
  templateUrl: './exam-page.component.html',
  styleUrls: ['./exam-page.component.css'],
})
export class ExamPageComponent {
  loading: boolean = true;
  isTeacher!: boolean;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private subjectService: SubjectService
  ) {
    const subjectId = Number(
      this.activatedRoute.snapshot.paramMap.get('subjectId')
    );
    authService.currentUserObserver().subscribe((user) => {
      this.subjectService.isSubjectTeacher(user.id, subjectId).subscribe(
        (response) => {
          this.isTeacher = response;
          this.loading = false;
        },
        (_) => {
          router.navigate(['/error']);
        }
      );
    });
  }
}
