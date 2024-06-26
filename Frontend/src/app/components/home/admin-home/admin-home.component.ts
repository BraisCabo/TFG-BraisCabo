
import { Component } from '@angular/core';
import { SubjectService } from 'src/app/services/SubjectService';
import { ConfirmDialog } from '../../dialogs/ConfirmDialog';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SubjectEditingDialog } from '../../edit-subject/edit-subject.component';
import { SubjectPageAdminComponent } from '../../subject-page-admin/subject-page-admin.component';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { SubjectDetailed } from 'src/app/models/SubjectDetailed';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css'],
})
export class AdminHomeComponent {
  subjects: SubjectDetailed[] = [];
  totalSize: number = 0;
  page: number = 0;
  pageSize: number = 5;
  name: string = '';
  loadingSubjects : boolean = true;

  constructor(
    private subjectService: SubjectService,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private router: Router
  ) {
    this.loadSubjects();
  }

  loadSubjects() {
    this.loadingSubjects = true;
    this.subjectService.getAllSubjects(this.name, this.page, this.pageSize).subscribe((data: any) => {
      this.totalSize = data.totalElements;
      this.subjects = data.content;
      this.loadingSubjects = false;
    },
    _ => {
      this.router.navigate(["/error"])
    });
  }

  searchSubjects() {
    this.page = 0;
    this.loadSubjects();
  }

  handlePageEventSubjects(e: PageEvent) {
    this.pageSize = e.pageSize;
    this.page = e.pageIndex;
    this.loadSubjects()
  }

  getTeachers(subject: SubjectDetailed): String {
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

  getStudents(subject: SubjectDetailed): String {
    if (subject.students.length == 0) {
      return 'No hay estudiantes asignados';
    }
    if (subject.students.length == 1) {
      return 'Hay 1 estudiante asignado.';
    } else {
      return 'Hay ' + subject.students.length + ' estudiantes asignados.';
    }
  }

  deleteSubject(subject: SubjectDetailed) {
    this.openDialogDelete(subject);
  }

  openDialogDelete(subject: SubjectDetailed): void {
    let dialogRef = this.dialog.open(ConfirmDialog, {
      data: { message: '¿Borrar la asignatura ' + subject.name + '?' },
      width: '250px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.subjectService.deleteSubjectById(subject.id).subscribe(
          (_) => {
            this.loadSubjects();
            this.openSnackBar('Asignatura ' + subject.name + ' eliminada.');
          },
          (_) => {
            this.openSnackBar(
              'Error al eliminar la asignatura ' + subject.name + '.'
            );
          }
        );
      }
    });
  }

  openDialogEdit(subject: SubjectDetailed): void {
    let dialogRef = this.dialog.open(SubjectEditingDialog, {
      data: { id: subject.id },
      width: '90%',
      height: '90%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadSubjects();
      }
    });
  }

  viewSubject(subject: SubjectDetailed) {
    this.openDialogView(subject);
  }

  openDialogView(subject: SubjectDetailed): void {
    this.dialog.open(SubjectPageAdminComponent, {
      data: { id: subject.id },
      width: '90%',
      height: '90%',
    });
  }

  editSubject(subject: SubjectDetailed) {
    this.openDialogEdit(subject);
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Aceptar', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 5000,
    });
  }

  addSubject() {
    this.subjects.push(new SubjectDetailed());
  }
}
