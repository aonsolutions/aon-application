import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { JwtAuthService } from 'src/app/core/services/jwt-auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from 'src/app/core/services/auth.service';

const loginInformation ={
  email : " ",
  password :" "
}
//mock a servicio que consume el loginComponent y sus metodos :
const mockedAuthService : {
  login              : () => void;

} = {
  login              : () => {loginInformation} ,
};
console.log(mockedAuthService);
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture  : ComponentFixture<LoginComponent>;//
  let service  : JwtAuthService;
  const routerSpy = {
    navigate   : jasmine.createSpy('navigate') //
  }

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
        {provide : AuthService, useValue : mockedAuthService}
      ],
      schemas : [CUSTOM_ELEMENTS_SCHEMA,NO_ERRORS_SCHEMA]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    //service = fixture.debugElement.injector.get(JwtAuthService);//instanciamos el servicio
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loginUser is defined', () => {
    component.loginUser();
    expect(component.auth).toBeDefined();
  });


  it('loginUser checks that the login method of AuthService mock is called', () => {
    const spyLogin = spyOn (mockedAuthService,'login');
    component.loginUser();
    expect(spyLogin).toHaveBeenCalled();

  });
});
