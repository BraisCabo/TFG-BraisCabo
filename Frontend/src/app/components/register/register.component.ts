import { Component } from '@angular/core';
import {
  FormControl,
  Validators,
  ValidatorFn,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

import { AuthService } from 'src/app/services/AuthService';

export function matchPasswordValidator(repeatPassword: any): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    let password = control;
    if (password && repeatPassword && password.value !== repeatPassword.value) {
      return { matchPassword: true };
    }
    return null;
  };
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  name = new FormControl('', [Validators.required]);
  repeatPassword = new FormControl('');
  password = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    matchPasswordValidator(this.repeatPassword as any),
  ]);
  email = new FormControl('', [Validators.required, Validators.email]);
  lastName = new FormControl('', [Validators.required]);
  hidePassword = true;
  hideRepeatPassword = true;
  loading = false;

  constructor(
    private authService: AuthService,
    private _snackBar: MatSnackBar,
    private router: Router
  ) {
    this.repeatPassword.valueChanges.subscribe((_) => {
      this.password.updateValueAndValidity();
    });
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, 'Aceptar', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 5000,
    });
  }

  register() {
    this.loading = true;
    this.authService
      .register(
        this.name.value ?? '',
        this.lastName.value ?? '',
        this.email.value ?? '',
        this.password.value ?? ''
      )
      .subscribe(
        (_) => {
          this.authService.loadUser();
          this.router.navigate(['/']);
        },
        (_) => {
          this.openSnackBar(
            'No se ha podido registrar el usuario por que el email ya está en uso'
          );
          this.loading = false;
        }
      );
  }

  getErrorMessageName() {
    return 'Este campo no puede estar vacío';
  }

  getErrorMessageLastName() {
    return 'Este campo no puede estar vacío';
  }

  getErrorMessageEmail() {
    if (this.email.hasError('required')) {
      return 'Este campo no puede estar vacío';
    }
    return 'El email no es válido';
  }

  getErrorMessagePassword() {
    if (this.password.hasError('required')) {
      return 'Este campo no puede estar vacío';
    }
    if (this.password.hasError('minlength')) {
      return 'La contraseña debe tener al menos 8 caracteres';
    }
    return 'Las contraseñas no coinciden';
  }

  correctData() {
    return (
      this.name.valid &&
      this.lastName.valid &&
      this.email.valid &&
      this.password.valid &&
      this.repeatPassword.valid
    );
  }
}
