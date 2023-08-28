import { Component } from '@angular/core';
import {
  FormControl,
  Validators,
  ValidatorFn,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

import { AuthService } from 'src/app/services/AuthService';
import { ConfirmDialog } from '../dialogs/ConfirmDialog';

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
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent {
  repeatPassword = new FormControl('');
  password = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    matchPasswordValidator(this.repeatPassword as any),
  ]);
  loading = false;
  hidePassword = true;
  hideRepeatPassword = true;

  constructor(
    private authService: AuthService,
    private _snackBar: MatSnackBar,
    private router: Router,
    private dialog: MatDialog,
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

  editPassword(){
      let dialogRef = this.dialog.open(ConfirmDialog, {
        data: { message: `¿Deseas cambiar esta calificación?` },
        width: '250px',
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.editPasswordLogic();
        }
      });
  }

  editPasswordLogic() {
    this.loading = true;
    this.authService
      .editUser(
        this.password.value ?? '',
        this.authService.getCurrentUser().id
      )
      .subscribe(
        (_) => {
          this.showMessage('Contraseña cambiada correctamente');
        },
        (_) => {
          this.showMessage('No se ha podido cambiar la contraseña, inténtelo de nuevo más tarde');
        }
      );
  }

  showMessage(message: string){
    this.password.reset();
    this.repeatPassword.reset();
    this.openSnackBar(
      message
    );
    this.loading = false;
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
      this.password.valid &&
      this.repeatPassword.valid
    );
  }
}
