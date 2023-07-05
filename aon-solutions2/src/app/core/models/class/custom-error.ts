import { IResponse } from "libraries/AonSDK/AonSDK";
import { ICustomError } from "../interface/icustom-error";

export class CustomError implements ICustomError{
    data: string | string[] = '' || [''];
    constructor(data: string | string[]){
        this.data = data;
    }
}
