import { AuthService } from 'src/app/services/AuthService';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {tap} from 'rxjs';

export const adminGuard = () => {
    const router = inject(Router);
    const service = inject(AuthService)
    return service.getMe().pipe(
    tap((value) => {
      if(value.roles.includes("ADMIN")){
        return true
      }
      else{
        router.navigate(['/error'])
        return false;
      }
    },
    (error) => {
      router.navigate(['/login'])
      return false
    }
  ))
}
