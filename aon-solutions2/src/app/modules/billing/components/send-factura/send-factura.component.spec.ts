import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendFacturaComponent } from './send-factura.component';

describe('SendFacturaComponent', () => {
  let component: SendFacturaComponent;
  let fixture: ComponentFixture<SendFacturaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SendFacturaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SendFacturaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
