import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing'
import { TranslateService } from '@ngx-translate/core';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
//  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
      ],
      declarations: [
        AppComponent,
        TranslateService
        
      ],
    }).compileComponents();

  });
/*
  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
 
  it('set predefined language', () => {
    const predefinedLanguage  = 'es';
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.predefinedLanguage).toBe(predefinedLanguage)
  });
*/
});
