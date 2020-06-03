import { Component, Input, OnInit } from '@angular/core';
@Component({
  selector: 'aon-icon-button',
  templateUrl: './aon-icon-button.component.html',
  styleUrls: ['./aon-icon-button.component.css'],
})
export class AonIconButtonComponent implements OnInit {
  @Input() icon: string;
  @Input() outline: boolean;
  hover: boolean = false;
  
  constructor() {

  }

  ngOnInit() {

  }
}
