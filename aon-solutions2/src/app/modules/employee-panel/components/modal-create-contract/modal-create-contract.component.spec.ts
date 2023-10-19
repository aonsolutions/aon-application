import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalCreateContractComponent } from './modal-create-contract.component';

describe('ModalCreateContractComponent', () => {
  let component: ModalCreateContractComponent;
  let fixture: ComponentFixture<ModalCreateContractComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalCreateContractComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalCreateContractComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
