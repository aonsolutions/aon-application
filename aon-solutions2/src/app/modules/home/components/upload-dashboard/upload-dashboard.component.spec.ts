import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadDashboardComponent } from './upload-dashboard.component';

describe('UploadDashboardComponent', () => {
  let component: UploadDashboardComponent;
  let fixture: ComponentFixture<UploadDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
