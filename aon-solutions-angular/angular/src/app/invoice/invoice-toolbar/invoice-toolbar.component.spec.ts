import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {InvoiceToolbarComponent} from './invoice-toolbar.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceToolbarComponent', () => {
  let component: InvoiceToolbarComponent;
  let fixture: ComponentFixture<InvoiceToolbarComponent>;

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
    fixture = TestBed.createComponent(InvoiceToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
