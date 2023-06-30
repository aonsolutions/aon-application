import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxDashboardComponent } from './inbox-dashboard.component';

describe('InboxDashboardComponent', () => {
  let component: InboxDashboardComponent;
  let fixture: ComponentFixture<InboxDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboxDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboxDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
