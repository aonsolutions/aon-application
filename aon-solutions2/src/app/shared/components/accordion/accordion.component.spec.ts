import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccordionComponent } from './accordion.component';
import { EventEmitter } from '@angular/core';

/*
describe('AccordionComponent', () => {
  let component: AccordionComponent;
  let fixture: ComponentFixture<AccordionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccordionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  // sendValue(){
  //   this.getState.emit(this.panelOpenState);
  // }
  it('must call send value', () => {
    const panelOpenState = false
    let event = new EventEmitter();
    component.getState.emit(panelOpenState);
    component.sendValue();
  });
});
*/