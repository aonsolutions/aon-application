import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-pagination-controller',
  templateUrl: './pagination-controller.component.html',
  styleUrls: ['./pagination-controller.component.scss']
})
export class PaginationControllerComponent implements OnInit {

  @Input() maxItems: number = 0;
  @Input() totalItems: number = 0;
  firstValue: number = 1;
  secondValue: number = 0;
  numPags: number = 0;
  actualPag: number = 1;
  @Output() pagValue = new EventEmitter<any>();
  @Output() totalPags = new EventEmitter<any>();

  constructor() {}

  ngOnInit(): void {
    this.secondValue = this.maxItems;
    if(this.totalItems % this.maxItems == 0){
      this.numPags = this.totalItems / this.maxItems;
    }else{
      this.numPags = this.totalItems / this.maxItems + 1;
    }
    this.numPags = Math.floor(this.numPags);
    this.totalPags.emit(this.numPags);
  }

  nextPage(){
    if(this.secondValue != this.totalItems){
      if((this.secondValue + this.maxItems) > this.totalItems){
        this.secondValue = this.totalItems;
        this.firstValue += this.maxItems;
      }else{
        this.secondValue += this.maxItems;
        this.firstValue += this.maxItems;
      }
      this.actualPag++;
      this.pagValue.emit(this.actualPag);
    }
  }

  previousPage(){
    if(this.firstValue != 1){
      if(this.secondValue == this.totalItems){
        this.secondValue = this.firstValue - 1;
        this.firstValue -= this.maxItems;
      }else{
        this.firstValue -= this.maxItems;
        this.secondValue -= this.maxItems;
      }
      this.actualPag--;
      this.pagValue.emit(this.actualPag);
    }
  }

}