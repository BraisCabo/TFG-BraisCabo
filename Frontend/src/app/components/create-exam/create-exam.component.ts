import { Component, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';

@Component({
  selector: 'app-create-exam',
  templateUrl: './create-exam.component.html',
  styleUrls: ['./create-exam.component.css']
})
export class CreateExamComponent {

  name : FormControl = new FormControl("", Validators.required)
  calificationPercentaje: FormControl = new FormControl(0, [Validators.required, Validators.max(100), Validators.min(0)])
  visibleExam: string = "false"
  calificationVisible : string = "false"
  openingDate: FormControl = new FormControl(Date, Validators.required)
  closingDate: FormControl = new FormControl(Date, Validators.required)
  questions: FormControl = new FormControl([])
  type : FormControl = new FormControl("UPLOADS", Validators.required)

  constructor(private _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) private _locale: string,) {
      this._locale = "es"
      this._adapter.setLocale(this._locale);

     }

  getNameError(){
    return "El nombre es obligatorio"
  }

  getCalificationPercentajeError(){
    if (this.calificationPercentaje.hasError('required')){
      return "Este campo es obligatorio"
    }
    if (this.calificationPercentaje.hasError('max')){
    return "No puede ser mayor que 100"
    }
      return "No puede ser menor que 0"
  }

}
