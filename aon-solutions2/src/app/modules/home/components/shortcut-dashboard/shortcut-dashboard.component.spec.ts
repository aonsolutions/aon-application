import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShortcutDashboardComponent } from './shortcut-dashboard.component';

describe('ShortcutDashboardComponent', () => {
  let component: ShortcutDashboardComponent;
  let fixture: ComponentFixture<ShortcutDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShortcutDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShortcutDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
