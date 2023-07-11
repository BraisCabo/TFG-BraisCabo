
export class ExamChanges{
  id: Number = new Number
  name: string = ""
  type: string = ""
  calificationPercentaje : string = ""
  visibleExam : boolean = false
  calificationVisible: boolean = true;

  openingDate : Date = new Date()

  closingDate : Date = new Date()

  questions : string[] = []

  examFile!: File

  deletedFile : boolean = false

  canRepeat: boolean = false

  canUploadLate : boolean = false

  questionsCalifications: String[] = []

  maxTime : string = ""

}
