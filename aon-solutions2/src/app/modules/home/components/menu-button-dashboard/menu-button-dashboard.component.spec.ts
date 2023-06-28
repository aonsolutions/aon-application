import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuButtonDashboardComponent } from './menu-button-dashboard.component';

describe('MenuButtonDashboardComponent', () => {
  let component: MenuButtonDashboardComponent;
  let fixture: ComponentFixture<MenuButtonDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MenuButtonDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuButtonDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
