import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {SureDialogComponent} from './sure-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('SureDialogComponent', () => {
  let component: SureDialogComponent;
  let fixture: ComponentFixture<SureDialogComponent>;

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
    fixture = TestBed.createComponent(SureDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
