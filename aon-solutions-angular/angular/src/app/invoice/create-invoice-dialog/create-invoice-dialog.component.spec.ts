import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateInvoiceDialogComponent} from './create-invoice-dialog.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('CreateInvoiceDialogComponent', () => {
  let component: CreateInvoiceDialogComponent;
  let fixture: ComponentFixture<CreateInvoiceDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AppModule],
      providers: [
       { provide: APP_BASE_HREF, useValue : '/' }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateInvoiceDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
