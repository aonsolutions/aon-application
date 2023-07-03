import { Deserializable } from "../interface/deserializable";

export class Document implements Deserializable{

    private file: string;
    private fileName: string;
    private fileSize: number;
    private fileType: string;
    private folder: string;
    private path: string;
    private date: Date;

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }
    
    public get File(): string {
        return this.file;
    }

    public set File(value: string) {
        this.file = value;
    }
    
    public get FileName(): string {
        return this.fileName;
    }

    public set FileName(value: string) {
        this.fileName = value;
    }
    
    public get FileSize(): number {
        return this.fileSize;
    }

    public set FileSize(value: number) {
        this.fileSize = value;
    }
    
    public get FileType(): string {
        return this.fileType;
    }

    public set FileType(value: string) {
        this.fileType = value;
    }

    public get Folder(): string {
        return this.folder;
    }
    
    public set Folder(value: string) {
        this.folder = value;
    }
    
    public get Path(): string {
        return this.path;
    }

    // public set Path(value: string) {
    //     this.path = value;
    // }

    constructor(file?: string,fileName?: string,fileSize?: number,fileType?: string, folder?: string, path?: string, date?: Date){
        this.file = file || '';
        this.fileName = fileName || '';
        this.fileSize = fileSize || 0;
        this.fileType = fileType || '';
        this.folder = folder || '';
        this.path = path || '';
        this.date = date || new Date();
    }

    deserialize(input: any): this {
        Object.assign(this, input);
        return this;
    }
    
    deserializeArray(input: any): Array<Document> {
        let array = []
        for(let i = 0; i < input.length; i++){
            let aux = new Document()
            aux.deserialize(input[i])
            array.push(aux)
        }
        return array;
    }

}
