import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {InvoiceFinanceDialogComponent} from './invoice-finance-dialog.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceFinanceDialogComponent', () => {
  let component: InvoiceFinanceDialogComponent;
  let fixture: ComponentFixture<InvoiceFinanceDialogComponent>;

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
    fixture = TestBed.createComponent(InvoiceFinanceDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
