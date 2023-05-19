import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent implements OnInit {

  @Input() width : string = 'auto';
  @Input() height : string = 'auto';
  @Input() color : string = '';
  @Input() backgroundColor : string = '';
  @Input() border : string = '';
  @Input() borderRadius : string = '';
  @Input() padding : string = '';
  @Input() hoverBackgroundColor : string = '';
  @Input() hoverBorderColor : string = '';
  @Input() fontSize : string = '1rem';
  @Input() align : string = '';

  constructor() {
  }

  ngOnInit(): void {
  }

}
