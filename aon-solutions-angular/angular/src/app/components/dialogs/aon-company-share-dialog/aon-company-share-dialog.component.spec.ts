import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AonCompanyShareDialogComponent} from './aon-company-share-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AonCompanyShareDialogComponent', () => {
  let component: AonCompanyShareDialogComponent;
  let fixture: ComponentFixture<AonCompanyShareDialogComponent>;

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
    fixture = TestBed.createComponent(AonCompanyShareDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
