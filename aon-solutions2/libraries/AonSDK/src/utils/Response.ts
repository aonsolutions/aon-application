import { IResponse } from "../interfaces/utilitiesInterfaces";
import { ERRORS } from "./Environment";

export class Response<T> implements IResponse<T> {
    code: string;
    description: string;
    result: T;
    constructor(data: any) {
            this.code = '0000';
            this.description = ERRORS['0000' as keyof typeof ERRORS].description;
            this.result = data;
    }
}

export class ErrorResponse implements IResponse<string> {
    code: string;
    description: string;
    result: string;
    constructor(data: any, model?: string) {
        // this.code = data as string;
        // this.description = ERRORS[data as keyof typeof ERRORS].description;
        // this.result = ERRORS[data as keyof typeof ERRORS].result;
        this.code = data as string;
        const errorData = ERRORS[data as keyof typeof ERRORS];
        this.description = errorData.description;
        this.result = errorData.result + (model ? ` (${model})` : '');
    }
}
