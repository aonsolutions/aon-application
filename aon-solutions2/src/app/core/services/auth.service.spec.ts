import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { AuthService } from './auth.service';
import { JwtAuthService } from './jwt-auth.service';
import { ApiService } from './api.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';



describe('AuthService', () => {
  let service : AuthService; //servicio
  let httpMock : HttpTestingController;//variable para simular solicitudes HTTP
  let router : Router;
  let apiService: ApiService;
  let jwtService: JwtAuthService;


  beforeEach(() => {

    TestBed.configureTestingModule({
      imports : [
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        AuthService,
        JwtAuthService,
        ApiService
      ],
      schemas : [CUSTOM_ELEMENTS_SCHEMA,NO_ERRORS_SCHEMA]
    });

  });
  //inyectamos el servicio authService
  beforeEach(()=>{
    service = TestBed.inject(AuthService); //instanciamos el servicio mockeado
    httpMock = TestBed.inject(HttpTestingController)//instancia del controlador mockeado
    router = TestBed.inject(Router);
    apiService = TestBed.inject(ApiService);
    jwtService = TestBed.inject(JwtAuthService);
  });
  //evita peticiones pendientes entre cada test. Para que no se lance el siguiente test mientras exista una pendiente.
  afterEach (() =>{
     httpMock.verify();

  });

  it ('should create',()=>{
    expect(service).toBeTruthy();
  });

  it('login behaves correctly when response is ok',()=>{
    let email = 'examplemail';
    let password = '123456';
    const requestBody = { //el login enviará el body
      username: email,
      password: password
    };

    const response = {//respuesta que recibiremos
      session_id : 'session_id'
    }

    const apiServiceSpy = spyOn(apiService,'post').and.returnValue(of(response));//espiamos el apiService para ver el metodo post que cuando sea llamado nos devolvera un observable con la response
    const jwtSpy = spyOn(jwtService,'setJwt');
    const navigateSpy = spyOn(router,'navigate');//espiamos el metodo navigate del router
    service.login(email,password); //llamada al login con los dos parametros que necesita

    expect(apiServiceSpy).toHaveBeenCalledWith('login', requestBody);
    expect(jwtService.setJwt).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/auth/selectEnterprise']);
  });

  it('login returns an error ',()=>{
    let email = 'examplemail';
    let password = '123456';
    const requestBody = { //cuerpo de request que necesita el login
      username: email,
      password: password
    };

    const errorResponse = new Error('http error!')//instanciamos un error
    const navigateSpy = spyOn(router,'navigate');//simulamos el metodo navidate de router
    const apiServiceSpy = spyOn(apiService,'post').and.returnValue(throwError(errorResponse));//espiamos el apiService para ver el metodo post que cuando sea llamado nos devolvera un observable con la response

    service.login(email,password); //llamada al login con los dos parametros que necesita

    expect(apiServiceSpy).toHaveBeenCalledWith('login', requestBody);//comprobamos si apiServiceSpy es llamado con los parametro que necesita
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it ('logout is called correctlty',()=>{
    const spy1 = spyOn(jwtService,'logout');//espiamos el metodo logout
    service.logout();//ejecutamos el metodo que queremos probar

    expect(jwtService.logout).toHaveBeenCalled();//verificamos si se ha llamado al jwt..
  });

  it('isLoggedIn calls jwtAuthService',()=>{
    const spyJwt = spyOn(jwtService,'isLoggedIn').and.returnValue(true);//espiamos el metodo isLoggedIn
    service.isLoggedIn();

    expect(spyJwt).toHaveBeenCalled();
  });

  it('getListEnterprises return list of enterprises when token exists',() => {
    const domainName = 'aahgd';
    const apiServiceSpy = spyOn(apiService,'getWithHeaders');
    let headers = new HttpHeaders();// objeto de tipo headers
        headers = headers.set('domain_name', domainName);

    service.getListEnterprises();

    expect(apiServiceSpy).toHaveBeenCalled();
    expect(apiServiceSpy).not.toEqual(null);

  });



});
