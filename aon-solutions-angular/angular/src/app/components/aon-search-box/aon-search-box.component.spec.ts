import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {AonSearchBoxComponent} from './aon-search-box.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('AonSearchBoxComponent', () => {
  let component: AonSearchBoxComponent;
  let fixture: ComponentFixture<AonSearchBoxComponent>;

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
    fixture = TestBed.createComponent(AonSearchBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
