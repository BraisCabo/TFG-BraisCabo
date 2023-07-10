
export class ImportedExam{
    name: string = "";
    calificationPercentaje: number= 0;
    visibleExam: boolean = false;
    calificationVisible: boolean= false;
    openingDate!: Date;
    closingDate!: Date;
    canUploadLate: boolean = false;
    maxTime : number = 1;
    file!: File;
}
