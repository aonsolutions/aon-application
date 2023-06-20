import { TestBed } from '@angular/core/testing';

import { ApiService } from './api.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpHeaders } from '@angular/common/http';

describe('ApiService', () => {
  let service: ApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports : [
        HttpClientTestingModule
      ]
    });
  });

  beforeEach (() => {
    service = TestBed.inject(ApiService);
  });
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('get method calls to the url + the method in parameter', ()=> {

    const spyHttp = spyOn(service,'get').and.callThrough();//lanza una llamada http
    service.get('method');

    expect(spyHttp).toHaveBeenCalledWith('method');
  });

  it('post method calls to the url + the method in parameter', ()=> {
    const spyHttp = spyOn(service,'post').and.callThrough();//lanza una llamada http
    const body = {
      data : 'data'
    }
    service.post('method',body);
    expect(spyHttp).toHaveBeenCalledWith('method',body);
  });
  // getWithHeaders(method: string, headers: HttpHeaders): Observable<any> {
  //   return this.http.get(this.urlBase+method, { headers });
  // }
  it('getWithHeaders method calls to the url + the method in parameter and headers',()=>{
    const headers = new HttpHeaders();// se crean los headers que se envian en la request
    const spyHttp = spyOn(service,'getWithHeaders').and.callThrough();//lanza una llamada http
    service.getWithHeaders('method',headers);

    expect(spyHttp).toHaveBeenCalledWith('method',headers);
  });
});
