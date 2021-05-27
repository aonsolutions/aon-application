import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {InvoiceTableComponent } from './invoice-table.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceTableComponent', () => {
  let component: InvoiceTableComponent;
  let fixture: ComponentFixture<InvoiceTableComponent>;

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
    fixture = TestBed.createComponent(InvoiceTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
