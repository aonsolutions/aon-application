import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {InvoiceOptionsDialogComponent} from './invoice-options-dialog.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceOptionsDialogComponent', () => {
  let component: InvoiceOptionsDialogComponent;
  let fixture: ComponentFixture<InvoiceOptionsDialogComponent>;

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
    fixture = TestBed.createComponent(InvoiceOptionsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
