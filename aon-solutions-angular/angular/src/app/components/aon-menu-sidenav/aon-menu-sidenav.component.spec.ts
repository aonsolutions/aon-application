import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AonMenuSidenavComponent} from './aon-menu-sidenav.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AonCompanyListComponent', () => {
  let component: AonMenuSidenavComponent;
  let fixture: ComponentFixture<AonMenuSidenavComponent>;

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
    fixture = TestBed.createComponent(AonMenuSidenavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
