import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DuplicateFacturaComponent } from './duplicate-factura.component';

describe('DuplicateFacturaComponent', () => {
  let component: DuplicateFacturaComponent;
  let fixture: ComponentFixture<DuplicateFacturaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DuplicateFacturaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DuplicateFacturaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
