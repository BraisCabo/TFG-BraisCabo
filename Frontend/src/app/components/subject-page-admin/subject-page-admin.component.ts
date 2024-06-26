import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { SubjectDetailed } from 'src/app/models/SubjectDetailed';
import { UserBasic } from 'src/app/models/UserBasic';
import { SubjectService } from 'src/app/services/SubjectService';

@Component({
  selector: 'app-subject-page-admin',
  templateUrl: './subject-page-admin.component.html',
  styleUrls: ['./subject-page-admin.component.css']
})
export class SubjectPageAdminComponent {

  public id!: number;
  name : String = "";
  teachers: UserBasic[] = [];
  students: UserBasic[] = [];
  public teacherPageSize : number = 5
  public teacherCurrentPage : number = 0
  public totalSizeT : number = 0
  public totalSizeS : number = 0
  teachersName : string = ''
  public studentPageSize : number = 5
  public studentCurrentPage : number = 0
  studentsName : string = ''

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { id: number },
    public dialogRef: MatDialogRef<SubjectPageAdminComponent>,
    private subjectService: SubjectService,
    private router: Router
  ) {
    this.id = data.id;
    this.subjectService.getSubjectById(this.id).subscribe((data: SubjectDetailed) => {
      this.name = data.name;
      this.loadTeachers()
      this.loadStudents()
    },
    _ =>{
      this.router.navigate(['/error'])
    });
  }

  loadTeachers(){
    this.subjectService.getSubjectTeachers(this.id,this.teachersName, this.teacherPageSize, this.teacherCurrentPage).subscribe((data: any) => {
      this.totalSizeT = data.totalElements;
      this.teachers = data.content;
    },
    _ => {
      this.router.navigate(['/error']);
    }
    );
  }

  loadStudents(){
    this.subjectService.getSubjectStudents(this.id,this.studentsName, this.studentPageSize, this.studentCurrentPage).subscribe((data: any) => {
      this.totalSizeS = data.totalElements;
      this.students = data.content;
    },
    _ => {
      this.router.navigate(['/error']);
    }
    );
  }

  handlePageEventTeachers(e: PageEvent) {
    this.teacherPageSize = e.pageSize;
    this.teacherCurrentPage = e.pageIndex;
    this.loadTeachers()
  }

  handlePageEventStudents(e: PageEvent) {
    this.studentPageSize = e.pageSize;
    this.studentCurrentPage = e.pageIndex;
    this.loadStudents()
  }

  closeDialog() {
    this.dialogRef.close();
  }

  searchTeachers(){
    this.teacherCurrentPage = 0;
    this.totalSizeT = 0;
    this.loadTeachers()
  }

  searchStudents(){
    this.studentCurrentPage = 0;
    this.totalSizeS = 0;
    this.loadStudents()
  }
}

