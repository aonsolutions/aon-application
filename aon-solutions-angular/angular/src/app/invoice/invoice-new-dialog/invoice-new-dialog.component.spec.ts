import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {InvoiceNewDialogComponent} from './invoice-new-dialog.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceNewDialogComponent', () => {
  let component: InvoiceNewDialogComponent;
  let fixture: ComponentFixture<InvoiceNewDialogComponent>;

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
    fixture = TestBed.createComponent(InvoiceNewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
