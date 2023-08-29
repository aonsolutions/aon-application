/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { InputProfileCompanyInformationComponent } from './input-profile-company-information.component';

describe('InputProfileCompanyInformationComponent', () => {
  let component: InputProfileCompanyInformationComponent;
  let fixture: ComponentFixture<InputProfileCompanyInformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InputProfileCompanyInformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InputProfileCompanyInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
