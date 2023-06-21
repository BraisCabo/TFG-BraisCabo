import { UserBasic } from "./UserBasic";

export class SubjectDetailed {
  id: number = 0;
  name: string = "";
  students: UserBasic[] = [];
  teachers: UserBasic[] = [];
}
