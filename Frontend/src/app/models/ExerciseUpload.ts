import { ExamTeacher } from "./ExamTeacher"
import { UserBasic } from "./UserBasic"

export class ExerciseUpload{
  id: number = 0
  calification: string = ""
  comment: string = ""
  uploadDate: Date = new Date()
  student: UserBasic = new UserBasic()
  uploaded : boolean = false
  exam : ExamTeacher = new ExamTeacher()
  questionsCalification : string[] = []

}
