import { BooleanInput } from '@angular/cdk/coercion';
import { Component } from '@angular/core';

import { UserService } from 'src/app/services/UserService';
import {PageEvent} from '@angular/material/paginator';
import {
  FormControl,
  Validators
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { SubjectService } from 'src/app/services/SubjectService';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserBasic } from 'src/app/models/UserBasic';

@Component({
  selector: 'app-create-subject',
  templateUrl: './create-subject.component.html',
  styleUrls: ['./create-subject.component.css'],
})
export class CreateSubjectComponent {
  teachers: Number[] = [];
  students: Number[] = [];
  allUsersT: UserBasic[] = [];
  allUsersS: UserBasic[] = [];
  name = new FormControl('', [Validators.required]);
  public teacherPageSize : number = 5
  public teacherCurrentPage : number = 0
  public totalSizeT : number = 0
  public totalSizeS : number = 0
  teachersName : string = ''

  public studentPageSize : number = 5
  public studentCurrentPage : number = 0
  studentsName : string = ''

  loadingTeachers : boolean = true;
  loadingStudents : boolean = true;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private subjectService: SubjectService,
    private router: Router

  ) {
    this.loadUsersTeachers()
    this.loadUsersStudents()
  }

  loadUsersTeachers(){
    this.loadingTeachers = true;
    this.userService.getAllUsers(this.teachersName, this.teacherPageSize, this.teacherCurrentPage).subscribe((data: any) => {
      this.totalSizeT = data.totalElements;
      this.allUsersT = data.content;
      this.loadingTeachers = false;
    },
    _ => {
      this.router.navigate(["/error"])
    }
    );
  }

  loadUsersStudents(){
    this.loadingStudents = true;
    this.userService.getAllUsers(this.studentsName, this.studentPageSize, this.studentCurrentPage).subscribe((data: any) => {
      this.totalSizeS = data.totalElements;
      this.allUsersS = data.content;
      this.loadingStudents = false;
    },
    _ => {
      this.totalSizeS = 0;
      this.allUsersS = [];
      this.loadingStudents = false;
    });
  }

  isTeacher(user: UserBasic): BooleanInput {
    return this.teachers.includes(user.id);
  }

  isStudent(user: UserBasic): BooleanInput {
    return this.students.includes(user.id);
  }

  addStudent(student: UserBasic) {
    if (this.students.includes(student.id)) {
      this.students.splice(this.students.indexOf(student.id), 1);
    } else {
      this.students.push(student.id);
    }
  }

  addTeacher(teacher: UserBasic) {
    if (this.teachers.includes(teacher.id)) {
      this.teachers.splice(this.students.indexOf(teacher.id), 1);
    } else {
      this.teachers.push(teacher.id);
    }
  }

  getErrorMessage() {
    return 'No puedes dejar este campo vacío';
  }

  canCreateSubject(): BooleanInput {
    return this.name.valid;
  }

  openDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Crear la asignatura ${this.name.value}?`},
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.subjectService
          .createSubject(this.name.value ?? '', this.students, this.teachers)
          .subscribe(
            (_) => {
              this.router.navigate(['/']);
            },
            (_) => {
              this.openSnackBar(
                "No se ha podido crear la asignatura por que ya existe una con ese nombre"
              );
            }
          );
      }
    });
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, "Aceptar", {
      horizontalPosition: "center",
      verticalPosition: "top",
      duration: 5000
    });
  }

  createSubject() {
    this.openDialog()
  }

  handlePageEventTeachers(e: PageEvent) {
    this.teacherPageSize = e.pageSize;
    this.teacherCurrentPage = e.pageIndex;
    this.loadUsersTeachers()
  }

  handlePageEventStudents(e: PageEvent) {
    this.studentPageSize = e.pageSize;
    this.studentCurrentPage = e.pageIndex;
    this.loadUsersStudents()
  }

  searchTeachers(){
    this.teacherCurrentPage = 0;
    this.totalSizeT = 0;
    this.loadUsersTeachers()
  }

  searchStudents(){
    this.studentCurrentPage = 0;
    this.totalSizeS = 0;
    this.loadUsersStudents()
  }
}
