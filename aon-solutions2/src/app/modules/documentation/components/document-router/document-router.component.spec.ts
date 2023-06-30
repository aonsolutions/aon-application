import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentRouterComponent } from './document-router.component';

describe('DocumentRouterComponent', () => {
  let component: DocumentRouterComponent;
  let fixture: ComponentFixture<DocumentRouterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentRouterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentRouterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
