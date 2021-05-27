import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {InvoiceDetailDialogComponent} from './invoice-detail-dialog.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceDetailDialogComponent', () => {
  let component: InvoiceDetailDialogComponent;
  let fixture: ComponentFixture<InvoiceDetailDialogComponent>;

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
    fixture = TestBed.createComponent(InvoiceDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
