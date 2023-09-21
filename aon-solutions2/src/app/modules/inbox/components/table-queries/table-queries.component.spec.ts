import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableQueriesComponent } from './table-queries.component';

describe('TableQueriesComponent', () => {
  let component: TableQueriesComponent;
  let fixture: ComponentFixture<TableQueriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableQueriesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableQueriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

//  it('should create', () => {
//    expect(component).toBeTruthy();
//  });
});
