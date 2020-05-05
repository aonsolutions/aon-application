import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AonCompanyListComponent} from './aon-company-list.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AonCompanyListComponent', () => {
  let component: AonCompanyListComponent;
  let fixture: ComponentFixture<AonCompanyListComponent>;

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
    fixture = TestBed.createComponent(AonCompanyListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
