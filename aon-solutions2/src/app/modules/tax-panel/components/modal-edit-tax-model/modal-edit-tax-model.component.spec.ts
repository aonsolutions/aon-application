import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalEditTaxModelComponent } from './modal-edit-tax-model.component';

describe('ModalEditTaxModelComponent', () => {
  let component: ModalEditTaxModelComponent;
  let fixture: ComponentFixture<ModalEditTaxModelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalEditTaxModelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalEditTaxModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

 // it('should create', () => {
  //  expect(component).toBeTruthy();
  //});
});
