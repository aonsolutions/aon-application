import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AdvancedUserSearchDialogComponent} from './advanced-user-search-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AdvancedUserSearchDialogComponent', () => {
  //let component: AdvancedUserSearchDialogComponent;
  let fixture: ComponentFixture<AdvancedUserSearchDialogComponent>;

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
    fixture = TestBed.createComponent(AdvancedUserSearchDialogComponent);
    //component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
