import { ActivatedRoute, Router } from '@angular/router';
import { Component, ViewChild } from '@angular/core';
import { StudentCalification } from 'src/app/models/StudentCalification';
import { SubjectService } from 'src/app/services/SubjectService';
import { MatTable } from '@angular/material/table';
import { TeacherCalifications } from 'src/app/models/TeacherCalifications';
export class ExamCalification {
  porcentaje!: String;
  examen!: String;
  calificacion!: String;
  comentario!: String;
}

@Component({
  selector: 'app-teacher-califications',
  templateUrl: './teacher-califications.component.html',
  styleUrls: ['./teacher-califications.component.css']
})
export class TeacherCalificationsComponent {
  @ViewChild(MatTable)
  table!: MatTable<ExamCalification>;
  userId : number = 0;
  subjectId: number = 0;
  califications: TeacherCalifications[] = [];
  displayedColumns: string[] = ['examen', 'porcentaje', 'calificacion', 'comentario'];
  source : ExamCalification[] = [];
  realSource : ExamCalification[][] = [];
  loading = true;

  constructor(private activatedRoute: ActivatedRoute, private subjectService: SubjectService, private router: Router) {
    this.userId = Number(this.activatedRoute.snapshot.paramMap.get('userId'));
    this.subjectId = Number(this.activatedRoute.snapshot.paramMap.get('subjectId'));
    this.subjectService.getTeacherSubjectCalifications(this.subjectId).subscribe((califications : any) => {
      this.califications = califications;
      for (let index = 0; index < this.califications.length; index++) {
        for(let i = 0; i < this.califications[index].studentCalifications.califications.length; i++) {
          this.source.push({examen: this.califications[index].studentCalifications.examNames[i], porcentaje: this.califications[index].studentCalifications.percentajes[i] + "%", calificacion: this.califications[index].studentCalifications.califications[i], comentario: this.califications[index].studentCalifications.comments[i]});
        }
        this.source.push({examen: "Calificación Final", porcentaje: "100%", calificacion: this.califications[index].studentCalifications.finalCalification, comentario: Number(this.califications[index].studentCalifications.finalCalification) > 5 ? "Aprobado" : "Suspenso"})
        this.realSource.push(this.source);
        this.source = [];
      }
      this.loading = false;
    }, (error) => {
      this.router.navigate(['/error']);
    }
    );
   }

   getCalifications(i: number): any{
    return this.realSource[i]
   }

   goToSubject() {
    this.router.navigate(['/subject/' + this.subjectId]);
  }
}
