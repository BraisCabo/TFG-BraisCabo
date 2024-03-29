import { AuthService } from './../../services/AuthService';
import { Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ExamService } from 'src/app/services/ExamService';
import { SubjectService } from 'src/app/services/SubjectService';
import { UserService } from 'src/app/services/UserService';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { MatDialog } from '@angular/material/dialog';
import { ExamBasic } from 'src/app/models/ExamBasic';

@Component({
  selector: 'app-subject-page',
  templateUrl: './subject-page.component.html',
  styleUrls: ['./subject-page.component.css'],
})
export class SubjectPageComponent {
  isTeacher: boolean = false;
  exams: ExamBasic[] = [];
  subjectId!: number;
  loadingExams = true;
  loadingIsTeacher = true

  constructor(
    private route: ActivatedRoute,
    private subjectService: SubjectService,
    private examService: ExamService,
    private router: Router,
    private authService: AuthService,
    private dialog: MatDialog
  ) {
    this.subjectId = Number(this.route.snapshot.paramMap.get('id'));
    this.examService.getSubjectExams(this.subjectId).subscribe(
      (exams) => {
        this.exams = exams;
        this.loadingExams = false;
      },
      (_) => {
        router.navigate(['/error']);
      }
    );
    this.authService.currentUserObserver().subscribe((user) => {
      if (user) {
        this.subjectService
          .isSubjectTeacher(authService.getCurrentUser().id, this.subjectId)
          .subscribe((isTeacher) => {
            this.isTeacher = isTeacher;
            this.loadingIsTeacher = false;
          });
      }else{
        router.navigate(['/error']);
      }
    });
  }

  isLoading() : boolean{
    return this.loadingExams || this.loadingIsTeacher;
  }

  changeVisibility(exam: ExamBasic) {
    if (exam.visibleExam){
      this.openDialog("¿Quieres ocultar el examen?", exam)
    }
    else {
      this.openDialog("¿Quieres hacer visible el examen?", exam)
    }
  }

  openDialog(message: String, exam: ExamBasic): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: message },
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        exam.visibleExam = !exam.visibleExam;
        this.examService.changeVisibility(this.subjectId, exam.id, exam.visibleExam).subscribe(
          (_) => {
          },
          (_) => {
            this.router.navigate(['/error']);
      });
    }}
    );
  }

  createExam(){
    this.router.navigate(['/subject/'+this.subjectId+'/newExam']);
  }

  importExam(){
    this.router.navigate(['/subject/'+this.subjectId+'/importExam']);
  }

  goToExam(exam: ExamBasic){
    this.router.navigate(['/subject/'+this.subjectId+'/exam/'+exam.id]);
  }

  goToCalifications(){
    this.router.navigate(['/subject/'+this.subjectId+'/user/'+ this.authService.getCurrentUser().id + '/califications']);
  }
}
