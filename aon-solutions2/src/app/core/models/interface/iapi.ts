import { HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs/internal/Observable";

export interface IApi {
  get(method: string): Observable<any>;
  getWithHeaders(url: string, headers: HttpHeaders): Observable<any>;
  post(method: string, data: any): Observable<any>;
}
