import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {PrinterConfigurationComponent} from './printer-configuration.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('PrinterConfigurationComponent', () => {
  let component: PrinterConfigurationComponent;
  let fixture: ComponentFixture<PrinterConfigurationComponent>;

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
    fixture = TestBed.createComponent(PrinterConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
