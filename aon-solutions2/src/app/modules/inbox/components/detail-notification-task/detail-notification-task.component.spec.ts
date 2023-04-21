import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailNotificationTaskComponent } from './detail-notification-task.component';

describe('DetailNotificationTaskComponent', () => {
  let component: DetailNotificationTaskComponent;
  let fixture: ComponentFixture<DetailNotificationTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailNotificationTaskComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailNotificationTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
