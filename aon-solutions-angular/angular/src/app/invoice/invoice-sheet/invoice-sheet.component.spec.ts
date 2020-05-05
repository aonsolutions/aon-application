import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {InvoiceSheetComponent } from './invoice-sheet.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('InvoiceSheetComponent', () => {
  let component: InvoiceSheetComponent;
  let fixture: ComponentFixture<InvoiceSheetComponent>;

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
    fixture = TestBed.createComponent(InvoiceSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
