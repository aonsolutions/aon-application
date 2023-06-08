import { HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs/internal/Observable";

export interface IApi {
  get(method: string, params?:any): Observable<any>;
  post(method: string, data: any): Observable<any>;
  setHeaders(): HttpHeaders;
}
