import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidenavLayoutComponent } from './sidenav-layout.component';

describe('SidenavLayoutComponent', () => {
  let component: SidenavLayoutComponent;
  let fixture: ComponentFixture<SidenavLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SidenavLayoutComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SidenavLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
