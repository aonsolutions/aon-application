import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteFacturaComponent } from './delete-factura.component';

describe('DeleteFacturaComponent', () => {
  let component: DeleteFacturaComponent;
  let fixture: ComponentFixture<DeleteFacturaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeleteFacturaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteFacturaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
