import { AuthService } from 'src/app/services/AuthService';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {tap} from 'rxjs';

export const loggedGuard = () => {
    const router = inject(Router);
    const service = inject(AuthService)
    return service.getMe().pipe(
    tap((value) => {
      return !value ? router.navigate(['/login']) : true
    },
    (error) => {
      router.navigate(['/login'])
    }
  ))
}
