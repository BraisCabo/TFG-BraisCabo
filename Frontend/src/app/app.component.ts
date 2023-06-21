import { Component, HostListener, ViewChild } from '@angular/core';
import { DrawerService } from './services/DrawerService';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(public drawerService:DrawerService){}
  title = 'Aula Virtual'

  backdropClick(){
    if (this.drawerService.opened){
      this.drawerService.opened = false;
    }
  }
}
