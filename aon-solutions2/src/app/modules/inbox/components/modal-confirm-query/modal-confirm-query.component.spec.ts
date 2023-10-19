import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalConfirmQueryComponent } from './modal-confirm-query.component';

describe('ModalConfirmQueryComponent', () => {
  let component: ModalConfirmQueryComponent;
  let fixture: ComponentFixture<ModalConfirmQueryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalConfirmQueryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalConfirmQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
