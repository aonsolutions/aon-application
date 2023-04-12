import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentInboxComponent } from './content-inbox.component';

describe('ContentInboxComponent', () => {
  let component: ContentInboxComponent;
  let fixture: ComponentFixture<ContentInboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContentInboxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentInboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
