import { Component, Inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';

@Component({
  selector: 'dialog-animations-example-dialog',
  templateUrl: 'ConfirmDialog.html',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
})
export class ConfirmDialog {

  public message: String = "";
  constructor(@Inject(MAT_DIALOG_DATA) public data: {message: string}, public dialogRef: MatDialogRef<ConfirmDialog>) {
    this.message = data.message;
  }

  closeDialog(event: boolean) {
    this.dialogRef.close(event);
  }
}
