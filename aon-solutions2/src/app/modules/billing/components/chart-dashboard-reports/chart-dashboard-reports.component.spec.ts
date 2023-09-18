import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartDashboardReportsComponent } from './chart-dashboard-reports.component';

describe('ChartDashboardReportsComponent', () => {
  let component: ChartDashboardReportsComponent;
  let fixture: ComponentFixture<ChartDashboardReportsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartDashboardReportsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartDashboardReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
