import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpinnerAwaitComponent } from './spinner-await.component';

describe('SpinnerAwaitComponent', () => {
  let component: SpinnerAwaitComponent;
  let fixture: ComponentFixture<SpinnerAwaitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpinnerAwaitComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpinnerAwaitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
