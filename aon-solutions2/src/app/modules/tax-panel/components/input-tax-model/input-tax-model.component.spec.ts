/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { InputTaxModelComponent } from './input-tax-model.component';

describe('InputTaxModelComponent', () => {
  let component: InputTaxModelComponent;
  let fixture: ComponentFixture<InputTaxModelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InputTaxModelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InputTaxModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
