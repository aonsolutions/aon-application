import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableInboxComponent } from './table-inbox.component';

describe('TableInboxComponent', () => {
  let component: TableInboxComponent;
  let fixture: ComponentFixture<TableInboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableInboxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableInboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
