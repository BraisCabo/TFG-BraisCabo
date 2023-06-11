import { Exam } from "./Exam";
import { User } from "./User";

export class Subject{
  id: Number = new Number();
  name: String = new String();
  teachers : User[] = [];
  students : User[] = [];
  exams : Exam[] = [];
}
