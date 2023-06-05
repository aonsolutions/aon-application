import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-icon',
  templateUrl: './icon.component.html',
  styleUrls: ['./icon.component.scss']
})
export class IconComponent implements OnInit {

  @Input() shape : string = '';
  @Input() size : string = '1.5rem';
  @Input() color : string = 'black';
  @Input() backgroundColor : string = '';
  @Input() border : string = '';
  @Input() borderRadius : string = '';
  @Input() padding : string = '';

  constructor() {}
  
  ngOnInit(): void {
  }

}
