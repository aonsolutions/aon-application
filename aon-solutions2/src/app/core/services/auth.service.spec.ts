
import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { AonSDK } from 'libraries/AonSDK/AonSDK';


describe('AuthService', () => {
  let service : AuthService; //servicio
  let httpMock : HttpTestingController;//variable para simular solicitudes HTTP
  let router : Router ;
  let aonSDK : AonSDK;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports : [
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        AuthService,
        AonSDK,
      ],
      schemas : [CUSTOM_ELEMENTS_SCHEMA,NO_ERRORS_SCHEMA]
    });

  });
  //inyectamos el servicio authService
  beforeEach(()=>{
    service = TestBed.inject(AuthService); //instanciamos el servicio mockeado
    httpMock = TestBed.inject(HttpTestingController)//instancia del controlador mockeado
    router = TestBed.inject(Router);
    aonSDK= TestBed.inject(AonSDK);
  });
  //evita peticiones pendientes entre cada test. Para que no se lance el siguiente test mientras exista una pendiente.
  afterEach (() =>{
     httpMock.verify();

  });

  it ('should create',()=>{
    expect(service).toBeTruthy();
  });

  // it('login behaves correctly when response is ok',()=>{
  //   const user = 'user';
  //   const password = 'password';
  //   const responseOK = {
  //     'code' : '0000',
  //     'description': '',
  //     'result':true
  //   };
  //   const spy = spyOn(aonSDK, 'model').and.returnValue(new Promise((resolve,reject) => {
  //           resolve(responseOK);
  //       }));

  //  const navigateSpy = spyOn(router,'navigate');
  //   service.login(user,password);
  //   expect(spy).toHaveBeenCalled();
  //   expect(navigateSpy).toHaveBeenCalledWith(['/auth/selectEnterprise']);

  // });

  it('logout',()=>{
    let aonSdk = new AonSDK();
    const spyAonSDK = spyOn(aonSDK, 'model');
    const callLogout =  service.logout();

    expect(callLogout).toHaveBeenCalled();
  });
});


