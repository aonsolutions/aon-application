import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AdvancedSearchDialogComponent} from './advanced-search-dialog.component';
import { AppModule } from '../../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AdvancedSearchDialogComponent', () => {
  let component: AdvancedSearchDialogComponent;
  let fixture: ComponentFixture<AdvancedSearchDialogComponent>;

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
    fixture = TestBed.createComponent(AdvancedSearchDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
