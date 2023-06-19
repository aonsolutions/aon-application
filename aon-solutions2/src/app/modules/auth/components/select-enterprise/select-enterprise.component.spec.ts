import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectEnterpriseComponent } from './select-enterprise.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from 'src/app/core/services/auth.service';
import { ApiService } from 'src/app/core/services/api.service';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

describe('SelectEnterpriseComponent', () => {
  let component: SelectEnterpriseComponent;
  let fixture: ComponentFixture<SelectEnterpriseComponent>;
  const routerSpy = {
    navigate : jasmine.createSpy('navigate') //
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
        ApiService,
        {provider : Router, useValue : routerSpy}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectEnterpriseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
