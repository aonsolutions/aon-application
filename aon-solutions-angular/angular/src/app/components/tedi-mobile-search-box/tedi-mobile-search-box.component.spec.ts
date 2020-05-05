import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TediMobileSearchBoxComponent} from './tedi-mobile-search-box.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('TediMobileSearchBoxComponent', () => {
  let component: TediMobileSearchBoxComponent;
  let fixture: ComponentFixture<TediMobileSearchBoxComponent>;

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
    fixture = TestBed.createComponent(TediMobileSearchBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
