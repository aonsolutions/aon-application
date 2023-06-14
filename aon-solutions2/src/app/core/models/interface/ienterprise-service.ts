import { Observable } from "rxjs/internal/Observable";
import { Enterprise } from "../class/enterprise";

export interface IEnterpriseService {
    getEnterprises(): Observable<Enterprise []>;
}
