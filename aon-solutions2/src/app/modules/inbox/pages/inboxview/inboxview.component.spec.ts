import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxviewComponent } from './inboxview.component';

describe('InboxviewComponent', () => {
  let component: InboxviewComponent;
  let fixture: ComponentFixture<InboxviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboxviewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InboxviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
