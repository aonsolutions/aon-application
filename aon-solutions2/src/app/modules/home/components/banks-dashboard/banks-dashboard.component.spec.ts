import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BanksDashboardComponent } from './banks-dashboard.component';

describe('BanksDashboardComponent', () => {
  let component: BanksDashboardComponent;
  let fixture: ComponentFixture<BanksDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BanksDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BanksDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
