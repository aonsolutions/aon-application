import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RateInboxComponent } from './rate-inbox.component';

describe('RateInboxComponent', () => {
  let component: RateInboxComponent;
  let fixture: ComponentFixture<RateInboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RateInboxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RateInboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
