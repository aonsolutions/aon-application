import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExampleLayoutComponent } from './example-layout.component';

describe('ExampleLayoutComponent', () => {
  let component: ExampleLayoutComponent;
  let fixture: ComponentFixture<ExampleLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExampleLayoutComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExampleLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
