import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BandejaComponent } from './bandeja.component';

describe('BandejaComponent', () => {
  let component: BandejaComponent;
  let fixture: ComponentFixture<BandejaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BandejaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BandejaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
