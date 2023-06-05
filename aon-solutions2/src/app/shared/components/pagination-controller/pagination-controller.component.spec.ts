import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaginationControllerComponent } from './pagination-controller.component';

describe('PaginationControllerComponent', () => {
  let component: PaginationControllerComponent;
  let fixture: ComponentFixture<PaginationControllerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaginationControllerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaginationControllerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
