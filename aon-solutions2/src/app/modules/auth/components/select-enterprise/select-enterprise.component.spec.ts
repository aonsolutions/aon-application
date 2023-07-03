import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectEnterpriseComponent } from './select-enterprise.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from 'src/app/core/services/auth.service';;
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
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
      declarations: [
        SelectEnterpriseComponent
      ],
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

  it('selectEnterprise has been called', () => {
    let enterprise = new Enterprise ('enterpriseName','documentName');
    spyOn(router,'navigate');
    const spyEnterpriseService = spyOn(enterpriseService,'setEnterprise');
    component.selectEnterprise(enterprise);

    expect(spyEnterpriseService).toBeTruthy();
  });
});
