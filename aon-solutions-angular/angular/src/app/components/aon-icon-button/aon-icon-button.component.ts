import { Component, Input, OnInit } from '@angular/core';
@Component({
  selector: 'aon-icon-button',
  templateUrl: './aon-icon-button.component.html',
  styleUrls: ['./aon-icon-button.component.css'],
})
export class AonIconButtonComponent implements OnInit {
  @Input() icon: string;
  @Input() color: string = '#5f6368';
  @Input() outline: boolean;
  @Input() noHover: boolean;
  hover: boolean = false;

  constructor() {

  }

  ngOnInit() {

  }
}
