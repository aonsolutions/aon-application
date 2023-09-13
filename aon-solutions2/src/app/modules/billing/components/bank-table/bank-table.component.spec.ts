import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankTableComponent } from './bank-table.component';

describe('BankTableComponent', () => {
  let component: BankTableComponent;
  let fixture: ComponentFixture<BankTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BankTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BankTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
