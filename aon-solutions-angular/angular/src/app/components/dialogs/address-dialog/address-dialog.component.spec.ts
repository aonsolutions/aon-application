import { async, ComponentFixture, TestBed} from '@angular/core/testing';
import { AddressDialogComponent} from './address-dialog.component';
import { AonMaker } from '../../../models/models';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';
import { MAT_DIALOG_DATA } from '@angular/material';


describe('AddresDialogComponent', () => {
  let component: AddressDialogComponent;
  let fixture: ComponentFixture<AddressDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AppModule],
      providers: [
       { provide: APP_BASE_HREF, useValue : '/' },
       { provide: MAT_DIALOG_DATA, useValue: AonMaker.createAddress() }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddressDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
