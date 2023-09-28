import { IMark, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { ApiHttpRequest } from "../utils/Http";
import { KeyGenerator } from "../utils/KeyGenerator";
import { GET_MULTIPLE, GET_METHOD } from "../utils/constants";

export class Mark implements IMark, IModel  {
    private key: string;
    private apiObject: any;
    private name: string;
    private idUser: string;
    private date: Date;
    private entryDate: Date;
    private exitDate: Date;
    private time: Date;
    private location: any;
    private status: string;

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get IdUser(): string {
        return this.idUser;
    }

    public set IdUser(value: string) {
        this.idUser = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get EntryDate(): Date {
        return this.entryDate;
    }

    public set EntryDate(value: Date) {
        this.entryDate = value;
    }

    public get ExitDate(): Date {
        return this.exitDate;
    }

    public set ExitDate(value: Date) {
        this.exitDate = value;
    }

    public get Time(): Date {
        return this.time;
    }

    public set Time(value: Date) {
        this.time = value;
    }

    public get Location(): any {
        return this.location;
    }

    public set Location(value: any) {
        this.location = value;
    }

    public get Status(): string {
        return this.status;
    }

    public set Status(value: string) {
        this.status = value;
    }

    constructor(name?: string, idUser?: string, date?: Date, entryDate?: Date, exitDate?: Date, time?: Date, location?: any, status?: string) {
        this.key = KeyGenerator.generate(15);
        this.name = name || '';
        this.idUser = idUser || '';
        this.date = date || new Date();
        this.entryDate = entryDate || new Date();
        this.exitDate = exitDate || new Date();
        this.time = time || new Date();
        this.location = location || '';
        this.status = status || '';
    }

    getKey(): string {
        return this.Key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('idUser', this.idUser);
        map.set('date', this.date);
        map.set('entryDate', this.entryDate);
        map.set('exitDate', this.exitDate);
        map.set('time', this.time);
        map.set('location', this.location);
        map.set('status', this.status);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('idUser', this.idUser);
        map.set('date', this.date);
        map.set('entryDate', this.entryDate);
        map.set('exitDate', this.exitDate);
        map.set('time', this.time);
        map.set('location', this.location);
        map.set('status', this.status);
        return map;
    }
}

// class Mark implements IMark, IModel  {
//     private id: string;
//     private name: string;
//     private lastName: string;
//     private idEmployee: string;
//     private date: Date;
//     private entryDate: Date;
//     private exitDate: Date;
//     private pause: IPause;
//     private location: string ;
//     private ccc: string;
//     private workplace: string ;
//     private status: string ;
//     private key: string;
//     protected apiObject: any;

//     public get ApiObject(): any {
//         return this.apiObject;
//     }

//     public set ApiObject(value: any) {
//         this.apiObject = value;
//     }


//     constructor(name?: string, lastName?: string, idEmployee?: string, date?: Date, entryDate?: Date, exitDate?: Date, pause?: IPause, location?: string, ccc?: string, workplace?: string, status?: string) {
//         this.id = KeyGenerator.generate(15);
//         this.name = name || '';
//         this.lastName = lastName || '';
//         this.idEmployee = idEmployee || '';
//         this.date = date || new Date();
//         this.entryDate = entryDate || new Date();
//         this.exitDate = exitDate || new Date();
//         this.pause = pause || { StartPause: new Date(), EndPause: new Date() };
//         this.location = location || '';
//         this.ccc = ccc || '';
//         this.workplace = workplace || '';
//         this.status = status || '';
//         this.key = this.id || '';
//     }

//     public get Id(): string {
//         return this.id;
//     }

//     public set Id(value: string) {
//         this.id = value;
//     }

//     public get Name(): string {
//         return this.name;
//     }

//     public set Name(value: string) {
//         this.name = value;
//     }

//     public get Lastname(): string {
//         return this.lastName;
//     }

//     public set Lastname(value: string) {
//         this.lastName = value;
//     }

//     public get IdEmployee(): string {
//         return this.idEmployee;
//     }

//     public set IdEmployee(value: string) {
//         this.idEmployee = value;
//     }

//     public get Date(): Date {
//         return this.date;
//     }

//     public set Date(value: Date) {
//         this.date = value;
//     }

//     public get EntryDate(): Date {
//         return this.entryDate;
//     }

//     public set EntryDate(value: Date) {
//         this.entryDate = value;
//     }

//     public get ExitDate(): Date {
//         return this.exitDate;
//     }

//     public set ExitDate(value: Date) {
//         this.exitDate = value;
//     }

//     public get Pause(): IPause {
//         return this.pause;
//     }

//     public set Pause(value: IPause) {
//         this.pause = value;
//     }

//     public get Location(): string {
//         return this.location;
//     }

//     public set Location(value: string) {
//         this.location = value;
//     }

//     public get Ccc(): string {
//         return this.ccc;
//     }

//     public set Ccc(value: string) {
//         this.ccc = value;
//     }

//     public get Workplace(): string {
//         return this.workplace;
//     }

//     public set Workplace(value: string) {
//         this.workplace = value;
//     }

//     public get Status(): string {
//         return this.status;
//     }

//     public set Status(value: string) {
//         this.status = value;
//     }

//     public get Key() {
//         return this.key;
//     }

//     public set Key(value: string) {
//         this.key = value;
//     }

//     getKey(): string {
//         return this.idEmployee;
//     }

//     getFilterableFields(): Map<string, any> {
//         let map = new Map<string, any>();
//         map.set('name', this.name);
//         map.set('lastname', this.lastName);
//         map.set('idEmployee', this.idEmployee);
//         map.set('date', this.date);
//         map.set('entryDate', this.entryDate);
//         map.set('exitDate', this.exitDate);
//         map.set('pause', this.pause);
//         map.set('location', this.location);
//         map.set('ccc', this.ccc);
//         map.set('workplace', this.workplace);
//         map.set('status', this.status);
//         return map;
//     }

//     getSortableFields(): Map<string, any> {
//         let map = new Map<string, any>();
//         map.set('name', this.name);
//         map.set('lastname', this.lastName);
//         map.set('idEmployee', this.idEmployee);
//         map.set('date', this.date);
//         map.set('entryDate', this.entryDate);
//         map.set('exitDate', this.exitDate);
//         map.set('pause', this.pause);
//         map.set('location', this.location);
//         map.set('ccc', this.ccc);
//         map.set('workplace', this.workplace);
//         map.set('status', this.status);
//         return map;
//     }

// }

export class ApiMark implements IApiModel {

    http = new ApiHttpRequest();

    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        if(currentMethod == GET_MULTIPLE && filter && filter.fields?.has('idUser')){
            const params = {
                group: 'DAY',
                startDate: '2023-09-06',
                endDate: '2023-09-06',
                active: true,
                user: filter.fields?.get('idUser')
            }
            return [this.http.makeURL('/ms/api/timecontrol/list-holder', params)];
        }
        if(currentMethod == GET_MULTIPLE){
            const params = {
                group: 'DAY',
                startDate: '2023-09-06',
                endDate: '2023-09-06',
                active: true
            }
            return [this.http.makeURL('/ms/api/timecontrol/list', params)];
        }
        throw new Error("Method not implemented.");
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE) return GET_METHOD
        throw new Error("Method not implemented.");
    }

    parseDataToSend(data: any, currentMethod?: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }

    parseDataToReceive(data: any, currentMethod?: string, filter?: IFilter | undefined) {
        let mark = new Mark();
        mark.ApiObject = data;
        mark.IdUser = data.task_holder.id ? data.task_holder.id : '';
        mark.Name = data.task_holder.name ? data.task_holder.name : '';
        mark.Location = data.coordinates ? data.coordinates : {};
        mark.Status = data.status ? data.status : '';
        mark.Date = data.date ? new Date(data.date) : new Date();
        mark.EntryDate
        mark.ExitDate
        return mark;
        throw new Error("Method not implemented.");
    }

    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return false;
    }

}

export class StorableMark extends Mark implements IStorable<Mark> {
    getCollection(): ICollection<Mark> {
        return marks;
    }
    getLocalStorage(): string {
        return 'marks';
    }
}

export let marks: ICollection<Mark> = new Collection<Mark>();
export function setMarks(value: any) { marks = value; };