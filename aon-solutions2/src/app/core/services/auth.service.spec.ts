import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { AonSDK } from 'libraries/AonSDK/AonSDK';

/*
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

  it('login behaves correctly when response is ok',(done: DoneFn)=>{
    const user = 'test@aonsolutions.test';
    const password = 'test';
    spyOn(router,'navigate');
    service.login(user,password).then(value => {
      expect(value).toBe(true);
      done();
    }).catch(
      (error) => {
        expect(error).toBeTruthy();
        done();
      }
    )
  });
  it('login redirect to /selectEnterprise when the promise is resolved', (done: DoneFn) => {
    const user = 'test@aonsolutions.test';
    const password = 'test';
    const spyRouter = spyOn(router, 'navigate');
    service.login(user, password).then(value => {
      expect(value).toBeTruthy();
      expect(spyRouter).toHaveBeenCalledWith(['/selectEnterprise']);
      done();
    });
  });

  it('isLoggedIn return true',  () =>{
    aonSDK.model('auth').login('test@aonsolutions.test','test');
  
    expect(service.isLoggedIn()).toBe(true);
  });
  // it('isLoggedIn return false',  () =>{
//   aonSDK.model('auth').logout();

//   expect(service.isLoggedIn()).toBe(false)
// });

// });
  
  // it('login returns an error ',(done: DoneFn)=>{
  // const user = 'test';
  // const password = 'testaa';
  // spyOn(router,'navigate');
  // service.login(user,password).then(value => {
  // expect(value).toBe(true);
  //   done();
  // }).catch(
  //    (error) => {
  // expect(error).toBeTruthy();
  //     done();
  //   }
  // )
  // it('logout method executes correctly',(done: DoneFn) =>{
//   spyOn(router,'navigate');

//   expect(service.logout()).toBe(void 0);
//   done();
// });
});








*/