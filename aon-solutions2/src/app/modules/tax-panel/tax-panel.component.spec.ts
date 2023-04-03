import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaxPanelComponent } from './tax-panel.component';

describe('TaxPanelComponent', () => {
  let component: TaxPanelComponent;
  let fixture: ComponentFixture<TaxPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TaxPanelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaxPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
