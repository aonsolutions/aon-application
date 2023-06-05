import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectEnterpriseComponent } from './select-enterprise.component';

describe('SelectEnterpriseComponent', () => {
  let component: SelectEnterpriseComponent;
  let fixture: ComponentFixture<SelectEnterpriseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectEnterpriseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectEnterpriseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
