import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailConsultComponent } from './detail-consult.component';

describe('DetailConsultComponent', () => {
  let component: DetailConsultComponent;
  let fixture: ComponentFixture<DetailConsultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailConsultComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailConsultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
