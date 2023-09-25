/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ModalTaxesDetailsComponent } from './modal-taxes-details.component';

describe('ModalTaxesDetailsComponent', () => {
  let component: ModalTaxesDetailsComponent;
  let fixture: ComponentFixture<ModalTaxesDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ModalTaxesDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalTaxesDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

 // it('should create', () => {
  //  expect(component).toBeTruthy();
  //});
});
