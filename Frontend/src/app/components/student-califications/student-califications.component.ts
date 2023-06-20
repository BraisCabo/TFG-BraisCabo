import { ActivatedRoute, Router } from '@angular/router';
import { Component, ViewChild } from '@angular/core';
import { StudentCalification } from 'src/app/models/StudentCalification';
import { SubjectService } from 'src/app/services/SubjectService';
import { MatTable } from '@angular/material/table';
export class ExamCalification {
  porcentaje!: String;
  examen!: String;
  calificacion!: String;
  comentario!: String;
}



@Component({
  selector: 'app-student-califications',
  templateUrl: './student-califications.component.html',
  styleUrls: ['./student-califications.component.css']
})
export class StudentCalificationsComponent {
  @ViewChild(MatTable)
  table!: MatTable<ExamCalification>;
  userId : number = 0;
  subjectId: number = 0;
  califications: StudentCalification = new StudentCalification();
  displayedColumns: string[] = ['examen', 'porcentaje', 'calificacion', 'comentario'];
  source : ExamCalification[] = [];

  constructor(private activatedRoute: ActivatedRoute, private subjectService: SubjectService, private router: Router) {
    this.userId = Number(this.activatedRoute.snapshot.paramMap.get('userId'));
    this.subjectId = Number(this.activatedRoute.snapshot.paramMap.get('subjectId'));
    this.subjectService.getStudentCalifications(this.userId, this.subjectId).subscribe((califications : any) => {
      this.califications = califications;
      for (let index = 0; index < this.califications.califications.length; index++) {
        this.source.push({examen: this.califications.examNames[index], porcentaje: this.califications.percentajes[index] + "%", calificacion: this.califications.califications[index], comentario: this.califications.comments[index]});
      }
      this.table.renderRows()
    }

    );
   }

   getCalifications(): any{
    return this.source
   }

   goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }


}
