import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-element',
  templateUrl: './list-element.component.html',
  styleUrls: ['./list-element.component.scss']
})
export class ListElementComponent implements OnInit {

  @Input() heightContainer : string;
  @Input() widthContainer : string;
  @Input() padding : string;
  @Input() color : string;
  @Input() border : string;
  @Input() borderRadius : string;
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';

  constructor() {
    this.heightContainer = '';
    this.widthContainer = '';
    this.padding = '';
    this.color = 'lightgrey';
    this.border = '';
    this.borderRadius = '0';
  }

  ngOnInit(): void {
    if(this.border != ''){
      this.border = this.border + ' solid';
    }
    this.widthHost = this.widthContainer;
    this.heightHost = this.heightContainer;
  }

}
