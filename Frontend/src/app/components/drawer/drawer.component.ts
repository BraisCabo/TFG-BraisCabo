import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SubjectDetailed } from 'src/app/models/SubjectDetailed';
import { AuthService } from 'src/app/services/AuthService';

import { SubjectService } from 'src/app/services/SubjectService';

@Component({
  selector: 'app-drawer',
  templateUrl: './drawer.component.html',
  styleUrls: ['./drawer.component.css']
})
export class DrawerComponent {

  studiedSubjects : SubjectDetailed[] = []
  teachedSubjects : SubjectDetailed[] = []

  constructor(private subjectService: SubjectService, private authService: AuthService, private router: Router) {
   }

   ngOnInit(){
    this.authService.currentUserObserver().subscribe((user) => {
      this.subjectService.getUserSubject(user.id).subscribe((response) => {
        this.studiedSubjects = response.studiedSubject;
        this.teachedSubjects = response.teachedSubject;
      });
    });

   }

  goToSubject(subject: SubjectDetailed) {
    this.router.navigate(['/subject/' + subject.id]);
  }

  isSubjects(){
    return this.studiedSubjects.length > 0 || this.teachedSubjects.length > 0;
  }

}
