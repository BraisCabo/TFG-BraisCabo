import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/AuthService';
import { SubjectService } from 'src/app/services/SubjectService';

@Component({
  selector: 'app-califications-page',
  templateUrl: './califications-page.component.html',
  styleUrls: ['./califications-page.component.css']
})
export class CalificationsPageComponent {
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
