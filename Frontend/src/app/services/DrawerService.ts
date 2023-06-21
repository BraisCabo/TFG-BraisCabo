import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DrawerService {
  opened : boolean = false;

  toggle(){
    this.opened = !this.opened;
  }
}
