import { TestBed } from '@angular/core/testing';

import { JwtAuthService } from './jwt-auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

describe('JwtAuthService', () => {
  let service: JwtAuthService;
  let router : Router;

  beforeEach(() => {
    TestBed.configureTestingModule({

      imports : [
        HttpClientTestingModule,
        RouterTestingModule
      ],
    });

  });

  beforeEach(()=>{
    router = TestBed.inject(Router);
    service = TestBed.inject(JwtAuthService);
    });
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('logout remove access token',()=>{
    const navigateSpy = spyOn(router,'navigateByUrl');//simulamos el navigateByUrl
    service.logout();
    expect(navigateSpy).toHaveBeenCalled();
  });

  it('isLoggedIn return true',()=>{
   const spyGetJwt = spyOn(service,'getJwt').and.returnValue(true);

   service.isLoggedIn();

   expect(service.isLoggedIn()).toBeTrue();
  });

  it('isLoggedIn return false',()=>{
    const spyGetJwt = spyOn(service,'getJwt').and.returnValue(false);

    service.isLoggedIn();

    expect(service.isLoggedIn()).toBeFalse();
   });

 it('getJwt returns true if the token exists',()=>{
  const spyGetITem = spyOn(Storage.prototype, 'getItem').and.returnValue('token');

  service.getJwt();

  expect(service.getJwt()).toBeTrue();
});
it('getJwt returns false if the token not exists',()=>{
  const spyGetITem = spyOn(Storage.prototype, 'getItem').and.returnValue(null);//espía el metodo getItem del prototipo storage
  const result = service.getJwt();//llamada al metodo

  expect(result).toBeFalse();// comprobamos que es falso, nunca vendrá a null
});


it('setJwt returns access token',()=>{
  const token = 'session_id'
  const spySetItem = spyOn(Storage.prototype,'setItem');

  service.setJwt(token);

  expect(spySetItem).toHaveBeenCalled();
  expect(spySetItem).toHaveBeenCalledWith(environment.localStorageJwt.accessToken,token);
});

});
