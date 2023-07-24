/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TableTaxModelComponent } from './table-tax-model.component';

describe('TableTaxModelComponent', () => {
  let component: TableTaxModelComponent;
  let fixture: ComponentFixture<TableTaxModelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TableTaxModelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableTaxModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
