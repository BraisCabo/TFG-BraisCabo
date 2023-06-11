import { BooleanInput } from '@angular/cdk/coercion';
import { Component } from '@angular/core';

import { User } from 'src/app/models/User';
import { UserService } from 'src/app/services/UserService';
import {
  FormControl,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';
import { SubjectService } from 'src/app/services/SubjectService';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-subject',
  templateUrl: './create-subject.component.html',
  styleUrls: ['./create-subject.component.css'],
})
export class CreateSubjectComponent {
  teachers: User[] = [];
  students: User[] = [];
  allUsers: User[] = [];
  name = new FormControl('', [Validators.required]);
  ocupado: boolean = true;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private subjectService: SubjectService,
    private _snackBar: MatSnackBar,
    private router: Router
  ) {
    this.userService.getAllUsers().subscribe((data: User[]) => {
      this.allUsers = data;
    });
  }

  isTeacher(user: User): BooleanInput {
    return this.teachers.includes(user);
  }

  isStudent(user: User): BooleanInput {
    return this.students.includes(user);
  }

  addStudent(student: User) {
    if (this.students.includes(student)) {
      this.students.splice(this.students.indexOf(student), 1);
    } else {
      this.students.push(student);
    }
  }

  addTeacher(teacher: User) {
    if (this.teachers.includes(teacher)) {
      this.teachers.splice(this.students.indexOf(teacher), 1);
    } else {
      this.teachers.push(teacher);
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
}
