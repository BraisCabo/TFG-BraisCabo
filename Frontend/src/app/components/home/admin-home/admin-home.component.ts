import { Subject } from './../../../models/Subject';
import { Component } from '@angular/core';
import { SubjectService } from 'src/app/services/SubjectService';
import { ConfirmDialog } from '../../dialogs/ConfirmDialog';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css'],
})
export class AdminHomeComponent {
  subjects: Subject[] = [];

  constructor(
    private subjectService: SubjectService,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar
  ) {
    this.loadSubjects();
  }

  loadSubjects() {
    this.subjectService.getAllSubjects().subscribe((data: Subject[]) => {
      this.subjects = data;
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

  getStudents(subject: Subject): String {
    if (subject.students.length == 0) {
      return 'No hay estudiantes asignados';
    }
    if (subject.students.length == 1) {
      return 'Hay 1 estudiante asignado.';
    } else {
      return 'Hay ' + subject.students.length + ' estudiantes asignados.';
    }
  }

  deleteSubject(subject: Subject) {
    this.openDialog(subject);
  }

  openDialog(subject: Subject): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: "¿Borrar la asignatura " + subject.name +"?" },
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.subjectService.deleteSubjectById(subject.id).subscribe(
          (_) => {
            this.loadSubjects()
            this.openSnackBar("Asignatura " + subject.name + " eliminada.");
          },
          (_) => {
            this.openSnackBar("Error al eliminar la asignatura " + subject.name + ".")
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

  addSubject() {
    this.subjects.push(new Subject());
  }
}
