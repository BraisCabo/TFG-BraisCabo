import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'src/app/models/Subject';
import { AuthService } from 'src/app/services/AuthService';
import { SubjectService } from 'src/app/services/SubjectService';

@Component({
  selector: 'app-user-home',
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent {

  studiedSubjects : Subject[] = []
  teachedSubjects : Subject[] = []
  loadingTeachers : boolean = true;

  constructor(private subjectService: SubjectService, private authService: AuthService, private router: Router) {
    this.subjectService.getUserSubject(authService.getCurrentUser().id).subscribe((response) => {
      this.studiedSubjects = response.studiedSubject;
      this.teachedSubjects = response.teachedSubject;
      this.loadingTeachers = false;
    },
    (_) => {
      this.router.navigate(['/error']);
    });
   }

   getTeachers(subject: Subject): String {
    if (subject.teachers.length == 0) {
      return 'No hay profesores asignados';
    }
    let teachers: String = 'Profesores: ';
    let i: number = 0;
    for (i; i < subject.teachers.length; i++) {
      if (i == subject.teachers.length - 1) {
        teachers = teachers.concat(
          subject.teachers[i].name.toString() +
            ' ' +
            subject.teachers[i].lastName.toString() +
            '.'
        );
      } else {
        teachers = teachers.concat(
          subject.teachers[i].name.toString() +
            ' ' +
            subject.teachers[i].lastName.toString() +
            ', '
        );
      }
    }
    return teachers;
  }

  goToSubject(subject: Subject) {
    this.router.navigate(['/subject/' + subject.id]);
  }

  isSubjects(){
    return this.studiedSubjects.length > 0 || this.teachedSubjects.length > 0;
  }

  isLoading() : boolean{
    return this.loadingTeachers;
  }

}
