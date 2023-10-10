import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadCompletedModalComponent } from './upload-completed-modal.component';

describe('UploadCompletedModalComponent', () => {
  let component: UploadCompletedModalComponent;
  let fixture: ComponentFixture<UploadCompletedModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadCompletedModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadCompletedModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
