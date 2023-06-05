import { Component, EventEmitter, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-accordion',
  templateUrl: './accordion.component.html',
  styleUrls: ['./accordion.component.scss']
})
export class AccordionComponent implements OnInit {

  panelOpenState: boolean = false;
  @Output() getState : EventEmitter<boolean> = new EventEmitter();


  constructor() { }

  ngOnInit(): void {
  }

  sendValue(){
    this.getState.emit(this.panelOpenState);    
  }

}
