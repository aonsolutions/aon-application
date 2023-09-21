import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableNotificationsComponent } from './table-notifications.component';

describe('TableNotificationsComponent', () => {
  let component: TableNotificationsComponent;
  let fixture: ComponentFixture<TableNotificationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableNotificationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableNotificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

 // it('should create', () => {
 //   expect(component).toBeTruthy();
 // });
});
