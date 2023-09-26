import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabsProfileCompanyComponent } from './tabs-profile-company.component';

describe('TabsProfileCompanyComponent', () => {
  let component: TabsProfileCompanyComponent;
  let fixture: ComponentFixture<TabsProfileCompanyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TabsProfileCompanyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TabsProfileCompanyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

 // it('should create', () => {
 //   expect(component).toBeTruthy();
 // });
});
