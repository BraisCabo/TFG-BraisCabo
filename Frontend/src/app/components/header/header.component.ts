import { Router, RouterModule } from '@angular/router';
import { Component, Input } from '@angular/core';
import { AuthService } from 'src/app/services/AuthService';
import { DrawerService } from 'src/app/services/DrawerService';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  constructor(public drawerService : DrawerService, private authService: AuthService, private router: Router) {
  }

  toggle(){
    this.drawerService.toggle();
  }

  isLogged(): boolean{
    return this.authService.isLogged();
  }

  navigateToRegister(){
    this.router.navigate(['/register']);
  }

  navigateToLogin(){
    this.router.navigate(['/login']);
  }
}