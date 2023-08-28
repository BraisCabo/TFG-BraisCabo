import { Router } from '@angular/router';
import { Component } from '@angular/core';
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

  userName(){
    return this.authService.getCurrentUser().name;
  }

  isAdmin(){
    return this.authService.isAdmin();
  }

  logout(){
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  changePassword(){
    this.router.navigate(['/changePassword']);
  }
}
