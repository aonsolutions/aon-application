import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RememberPasswordDialogComponent} from './remember-password-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('RememberPasswordDialogComponent', () => {
  let component: RememberPasswordDialogComponent;
  let fixture: ComponentFixture<RememberPasswordDialogComponent>;

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
    fixture = TestBed.createComponent(RememberPasswordDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
