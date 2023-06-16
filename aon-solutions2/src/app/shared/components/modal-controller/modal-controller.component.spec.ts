import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalControllerComponent } from './modal-controller.component';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';

class MatDialogMock {
  open = jasmine.createSpy().and.returnValue({
    componentInstance : {
      afterClosed : () => of({
         data : 'closed'
      })
    },
  });
}

describe('ModalControllerComponent', () => {
  let component: ModalControllerComponent;
  let fixture: ComponentFixture<ModalControllerComponent>;
  const matDialog = new MatDialogMock();


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalControllerComponent ],
      imports : [

      ],
      providers : [
        {provide : MatDialog, useValue : matDialog}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalControllerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
