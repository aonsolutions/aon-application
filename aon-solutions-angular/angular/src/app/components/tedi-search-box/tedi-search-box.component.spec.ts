import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TediSearchBoxComponent} from './tedi-search-box.component';
import { AppModule } from '../../app.module';
import { APP_BASE_HREF } from '@angular/common';

describe('TediSearchBoxComponent', () => {
  let component: TediSearchBoxComponent;
  let fixture: ComponentFixture<TediSearchBoxComponent>;

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
    fixture = TestBed.createComponent(TediSearchBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });
});
