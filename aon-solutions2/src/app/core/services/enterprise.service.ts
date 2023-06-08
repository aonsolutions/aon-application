import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ApiService } from './api.service';
import { Enterprise } from '../models/class/enterprise';
import { catchError, map } from 'rxjs/operators';
import { IEnterpriseService } from '../models/interface/ienterprise-service';

@Injectable({
  providedIn: 'root'
})
export class EnterpriseService implements IEnterpriseService{

  constructor(public apiService: ApiService) { }

  getEnterprises(): Observable<Enterprise[]> {
    return this.apiService.get('company').pipe(
      map((res) => {
        for(let i = 0; i < res.length; i++){
          res[i] = new Enterprise().deserialize(res[i])
        }
        return res;
      }
      ),
      catchError((err, caught) => {
        //TODO: pendiente de ver como gestionar los errores, en cada llamada, en un interceptor, de forma general en los métodos de ApiService...
        throw new Error(err);
        console.log(err, caught)
        return EMPTY;
      })
    );
  }

}
