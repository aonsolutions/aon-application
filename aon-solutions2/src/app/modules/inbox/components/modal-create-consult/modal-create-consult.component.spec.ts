import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalCreateConsultComponent } from './modal-create-consult.component';

describe('ModalCreateConsultComponent', () => {
  let component: ModalCreateConsultComponent;
  let fixture: ComponentFixture<ModalCreateConsultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalCreateConsultComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalCreateConsultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
