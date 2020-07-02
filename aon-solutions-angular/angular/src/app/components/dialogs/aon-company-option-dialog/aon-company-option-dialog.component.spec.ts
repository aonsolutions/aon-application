import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AonCompanyOptionDialogComponent} from './aon-company-option-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AonCompanyOptionDialogComponent', () => {
  let component: AonCompanyOptionDialogComponent;
  let fixture: ComponentFixture<AonCompanyOptionDialogComponent>;

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
    fixture = TestBed.createComponent(AonCompanyOptionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
