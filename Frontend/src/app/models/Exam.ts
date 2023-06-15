
export class Exam{
  id: Number = new Number
  name: string = ""
  type: string = ""
  calificationPercentaje : string = ""
  visibleExam : boolean = false
  calificationVisible: boolean = true;

  openingDate : Date = new Date()

  closingDate : Date = new Date()

  questions : string[] = []

}
