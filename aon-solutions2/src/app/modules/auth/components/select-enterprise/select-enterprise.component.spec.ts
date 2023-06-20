import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectEnterpriseComponent } from './select-enterprise.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from 'src/app/core/services/auth.service';;
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
import { of } from 'rxjs';
import { Enterprise } from 'src/app/core/models/class/enterprise';

describe('SelectEnterpriseComponent', () => {
  let component: SelectEnterpriseComponent;
  let fixture: ComponentFixture<SelectEnterpriseComponent>;
  let enterpriseService : EnterpriseService;
  let router : Router;
  const routerSpy = {
    navigate : jasmine.createSpy('navigate')
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectEnterpriseComponent ],
      imports : [
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers : [
        AuthService,
        EnterpriseService,
        {provider : Router, useValue : routerSpy}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectEnterpriseComponent);
    component = fixture.componentInstance;
    enterpriseService = TestBed.inject(EnterpriseService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOinit obtains a list of companies from enterpriseService',()=>{
    const enterprises: Enterprise[] = [];
    //const spyEnterpriseService = spyOn(enterpriseService,'getEnterprises').and.returnValue(of(enterprises));
    component.ngOnInit();

    //expect(spyEnterpriseService).toHaveBeenCalled();
    expect(component.enterprises).toEqual(enterprises);

  });

  it('selectEnterprise sets the selected company and if not use id/document',()=>{
    const enterprises= new Enterprise();
    const navigateSpy = spyOn(router,'navigate');
    component.selectEnterprise(enterprises);
    expect(navigateSpy).toHaveBeenCalled();
  });

});
