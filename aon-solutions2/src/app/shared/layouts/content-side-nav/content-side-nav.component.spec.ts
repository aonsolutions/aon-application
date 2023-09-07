import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentSideNavComponent } from './content-side-nav.component';

describe('ContentSideNavComponent', () => {
  let component: ContentSideNavComponent;
  let fixture: ComponentFixture<ContentSideNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContentSideNavComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentSideNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
/*
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  */
});
