import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { CreateSubjectComponent } from './components/create-subject/create-subject.component';

const routes: Routes = [
  { path: '', component: HomeComponent},
  { path: 'login', component : LoginComponent },
  { path: 'newSubject', component: CreateSubjectComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
