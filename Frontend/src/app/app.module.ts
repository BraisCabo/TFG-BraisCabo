import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import { HeaderComponent } from './components/header/header.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {HttpClientModule } from '@angular/common/http';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSelectModule} from '@angular/material/select';
import {MatFormFieldModule} from '@angular/material/form-field';
import { LoginComponent } from './components/login/login.component';
import { FooterComponent } from './components/footer/footer.component';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatDialogModule} from '@angular/material/dialog';
import { HomeComponent } from './components/home/home.component';
import { AdminHomeComponent } from './components/home/admin-home/admin-home.component';
import { UserHomeComponent } from './components/home/user-home/user-home.component';
import { MatSnackBarModule} from '@angular/material/snack-bar';
import { CreateSubjectComponent } from './components/create-subject/create-subject.component';
import {MatDividerModule} from '@angular/material/divider';
import {MatListModule} from '@angular/material/list';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { ReactiveFormsModule } from '@angular/forms';
import { SubjectEditingDialog } from './components/edit-subject/edit-subject.component';
import { SubjectPageAdminComponent } from './components/subject-page-admin/subject-page-admin.component';
import { MatPaginatorModule } from '@angular/material/paginator';
import {MatMenuModule} from '@angular/material/menu';
import { RegisterComponent } from './components/register/register.component';
import { DrawerComponent } from './components/drawer/drawer.component';
import { SubjectPageComponent } from './components/subject-page/subject-page.component';
import { ErrorComponentComponent } from './components/error-component/error-component.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { CreateExamComponent } from './components/create-exam/create-exam.component';
import {MatRadioModule} from '@angular/material/radio';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {NgxMaterialTimepickerModule} from 'ngx-material-timepicker';
import { ExamPageComponent } from './components/exam-page/exam-page.component';
import { TeacherExamPageComponent } from './components/exam-page/teacher-exam-page/teacher-exam-page.component';
import { StudentExamPageComponent } from './components/exam-page/student-exam-page/student-exam-page.component';
import { EditExamComponent } from './components/edit-exam/edit-exam.component';
import { QuestionsExamPageComponent } from './components/questions-exam-page/questions-exam-page.component';
import { ViewAnswersDialogComponent } from './components/exam-page/student-exam-page/view-answers-dialog/view-answers-dialog.component';
import { CalificateComponent } from './components/calificate/calificate.component';
import { SetCalificationDialogComponent } from './components/calificate/set-calification-dialog/set-calification-dialog.component';
import { EditCalificationDialogComponent } from './components/calificate/edit-calification-dialog/edit-calification-dialog.component';
import { StudentCalificationsComponent } from './components/califications-page/student-califications/student-califications.component';
import {MatTableModule} from '@angular/material/table';
import { CalificationsPageComponent } from './components/califications-page/califications-page.component';
import { TeacherCalificationsComponent } from './components/califications-page/teacher-califications/teacher-califications.component';
import { SetCalificationDialogQuestionsComponent } from './components/calificate/set-calification-dialog-questions/set-calification-dialog-questions.component';
import { EditCalificationDialogQuestionsComponent } from './components/calificate/edit-calification-dialog-questions/edit-calification-dialog-questions.component';
import { ImportExamComponent } from './components/import-exam/import-exam.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    FooterComponent,
    HomeComponent,
    AdminHomeComponent,
    UserHomeComponent,
    CreateSubjectComponent,
    SubjectEditingDialog,
    SubjectPageAdminComponent,
    RegisterComponent,
    DrawerComponent,
    SubjectPageComponent,
    ErrorComponentComponent,
    CreateExamComponent,
    ExamPageComponent,
    TeacherExamPageComponent,
    StudentExamPageComponent,
    EditExamComponent,
    QuestionsExamPageComponent,
    ViewAnswersDialogComponent,
    CalificateComponent,
    SetCalificationDialogComponent,
    EditCalificationDialogComponent,
    StudentCalificationsComponent,
    CalificationsPageComponent,
    TeacherCalificationsComponent,
    SetCalificationDialogQuestionsComponent,
    EditCalificationDialogQuestionsComponent,
    ImportExamComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatToolbarModule,
    HttpClientModule,
    MatGridListModule,
    MatIconModule,
    MatSidenavModule,
    MatSelectModule,
    MatFormFieldModule,
    MatCardModule,
    MatInputModule,
    MatDialogModule,
    MatSnackBarModule,
    MatDividerModule,
    MatListModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    MatPaginatorModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatNativeDateModule,
    MatDatepickerModule,
    NgxMaterialTimepickerModule.setOpts('es-ES', 'spanish'),
    MatTableModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
