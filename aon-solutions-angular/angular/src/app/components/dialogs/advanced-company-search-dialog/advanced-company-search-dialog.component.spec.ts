import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AdvancedCompanySearchDialogComponent} from './advanced-company-search-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AdvancedCompanySearchDialogComponent', () => {
  //let component: AdvancedCompanySearchDialogComponent;
  let fixture: ComponentFixture<AdvancedCompanySearchDialogComponent>;

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
    fixture = TestBed.createComponent(AdvancedCompanySearchDialogComponent);
    //component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
