import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablesInboxComponent } from './table-inbox.component';

describe('TableInboxComponent', () => {
  let component: TablesInboxComponent;
  let fixture: ComponentFixture<TablesInboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TablesInboxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TablesInboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
