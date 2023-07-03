
export class ExamTeacher{
  id: Number = new Number
  name: string = ""
  type: string = ""
  calificationPercentaje : string = ""
  visibleExam : boolean = false
  calificationVisible: boolean = true;

  openingDate : Date = new Date()

  closingDate : Date = new Date()

  questions : string[] = []

  exerciseUploads : number = 0

  examFile : string = ""

  canRepeat: boolean = false

  canUploadLate : boolean = false

  questionsCalifications: String[] = []

}
