import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListDashboardComponent } from './list-dashboard.component';

describe('ListDashboardComponent', () => {
  let component: ListDashboardComponent;
  let fixture: ComponentFixture<ListDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  //it('should create', () => {
  //  expect(component).toBeTruthy();
  //});
  // Case 1
  /*
  it('should set icon based on type', () => {
    component.type = 1;
    component.ngOnInit();
    expect(component.icon).toBe('playlist_add_check');

  });
  // Case 2
  it('should set icon based on type', () => {
    component.type = 2;
    component.ngOnInit();
    expect(component.icon).toBe('message');

  });
  //case 3
  it('should set icon based on type', () => {
    component.type = 3;
    component.ngOnInit();
    expect(component.icon).toBe('notifications');

  });*/
});
