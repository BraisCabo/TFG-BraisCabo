import { BooleanInput } from '@angular/cdk/coercion';
import { Component, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { User } from 'src/app/models/User';
import { SubjectService } from 'src/app/services/SubjectService';
import { UserService } from 'src/app/services/UserService';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { Subject } from 'src/app/models/Subject';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';

@Component({
  selector: 'dialog-animations-example-dialog',
  templateUrl: 'edit-subject.component.html',
  styleUrls: ['./edit-subject.component.css'],

})
export class SubjectEditingDialog {
  id: number;
  teachers: Number[] = [];
  students: Number[] = [];
  allUsersT: User[] = [];
  allUsersS: User[] = [];
  name = new FormControl('', [Validators.required]);
  public teacherPageSize : number = 5
  public teacherCurrentPage : number = 0
  public totalSizeT : number = 0
  public totalSizeS : number = 0
  teachersName : string = ''
  loadingTeachers : boolean = true;
  loadingStudents : boolean = true;

  public studentPageSize : number = 5
  public studentCurrentPage : number = 0
  studentsName : string = ''

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { id: number },
    public dialogRef: MatDialogRef<SubjectEditingDialog>,
    private userService: UserService,
    private dialog: MatDialog,
    private subjectService: SubjectService,
    private _snackBar: MatSnackBar,
    private router : Router
  ) {
    this.id = data.id;
    this.subjectService.getSubjectById(this.id).subscribe((data: Subject) => {
      this.name.setValue(data.name.toString());
      this.teachers = data.teachers.map((teacher) => teacher.id);
      this.students = data.students.map((student) => student.id);
    });
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
      this.router.navigate(['/error']);
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

  closeDialog(event: boolean) {
    this.dialogRef.close(event);
  }

  isTeacher(user: User): BooleanInput {
    return this.teachers.includes(user.id);
  }

  isStudent(user: User): BooleanInput {
    return this.students.includes(user.id);
  }

  addStudent(student: User) {
    if (this.students.includes(student.id)) {
      this.students.splice(this.students.indexOf(student.id), 1);
    } else {
      this.students.push(student.id);
    }
  }

  addTeacher(teacher: User) {
    if (this.teachers.includes(teacher.id)) {
      this.teachers.splice(this.students.indexOf(teacher.id), 1);
    } else {
      this.teachers.push(teacher.id);
    }
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

  getErrorMessage() {
    return 'No puedes dejar este campo vacío';
  }

  canEditSubject(): BooleanInput {
    return this.name.valid;
  }

  openDialog(): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: `¿Editar la asignatura ${this.name.value}?` },
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.subjectService
          .editSubject(this.name.value ?? '', this.students, this.teachers, this.id)
          .subscribe(
            (_) => {
              this.closeDialog(true);
            },
            (_) => {
              this.openSnackBar(
                'No se ha podido editar la asignatura por que ya existe una con ese nombre'
              );
            }
          );
      }
    });
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Aceptar', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 5000,
    });
  }

  editSubject() {
    this.openDialog();
  }

  cancellEdition(){
    this.closeDialog(false);
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
