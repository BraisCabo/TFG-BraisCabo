import { NgModule } from '@angular/core';
import { RouterModule, Routes, CanActivate } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { CreateSubjectComponent } from './components/create-subject/create-subject.component';
import { RegisterComponent } from './components/register/register.component';
import { ErrorComponentComponent } from './components/error-component/error-component.component';
import { SubjectPageComponent } from './components/subject-page/subject-page.component';
import { CreateExamComponent } from './components/create-exam/create-exam.component';
import { ExamPageComponent } from './components/exam-page/exam-page.component';
import { loggedGuard } from './services/LoggedGuard';
import { adminGuard } from './services/AdminGuard';

const routes: Routes = [
  { path: '', component: HomeComponent, canActivate: [loggedGuard]},
  { path: 'login', component : LoginComponent },
  { path: 'newSubject', component: CreateSubjectComponent, canActivate: [adminGuard]},
  { path: 'register', component: RegisterComponent},
  { path: 'error', component: ErrorComponentComponent},
  { path: 'subject/:id', component: SubjectPageComponent, canActivate: [loggedGuard]},
  { path: 'subject/:id/newExam', component: CreateExamComponent, canActivate: [loggedGuard]},
  { path: 'subject/:subjectId/exam/:examId', component: ExamPageComponent, canActivate: [loggedGuard]},
  { path: '**', redirectTo: 'error'}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
