import { Component } from '@angular/core';
import { AuthService } from 'src/app/services/AuthService';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent {

  constructor(private authService: AuthService) { }

  isAdmin() : boolean{
    return this.authService.isAdmin();
  }
}
