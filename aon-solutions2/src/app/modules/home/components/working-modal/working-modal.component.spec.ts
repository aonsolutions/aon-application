import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkingModalComponent } from './working-modal.component';

describe('WorkingModalComponent', () => {
  let component: WorkingModalComponent;
  let fixture: ComponentFixture<WorkingModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkingModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkingModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
