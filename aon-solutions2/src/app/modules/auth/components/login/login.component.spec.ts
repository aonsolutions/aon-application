import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from 'src/app/core/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';


const loginInformation ={
  email : " ",
  password :" "
}
const magicLinkInformation ={
  token : ''
}
//mock a servicio que consume el loginComponent y sus metodos :
const mockedAuthService : {
  login              : () => void;
  magickLogin        : () => Promise<boolean>;

} = {
  login              : () => {loginInformation} ,
  magickLogin        : () => Promise.reject(magicLinkInformation)
};
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture  : ComponentFixture<LoginComponent>;//
  const routerSpy = {
    navigate   : jasmine.createSpy('navigate') //
  }
  let activatedRoute: Partial<ActivatedRoute>;//objeto parcial ActivatedRoute
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  activatedRoute = {
    queryParams: of({ token: 'testToken' }) // Simula el objeto de queryParams con el token
  };
  authServiceSpy = jasmine.createSpyObj('AuthService', ['magicLogin','login']);


  beforeEach(async () => {
    await TestBed.configureTestingModule({

      declarations: [
        LoginComponent
      ],
      imports : [
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers : [
        {provide : AuthService, useValue : mockedAuthService},
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: AuthService, useValue: authServiceSpy }
      ],
      schemas : [CUSTOM_ELEMENTS_SCHEMA,NO_ERRORS_SCHEMA]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loginUser checks that the login method of AuthService mock is called', () => {
    const spyLogin = authServiceSpy.login;
    component.loginUser();

    expect(spyLogin).toHaveBeenCalled();
  });
  it('should call magicLogin when token parameter token is defined', () => {
    expect(authServiceSpy.magicLogin).toHaveBeenCalledWith('testToken');
  });
});
