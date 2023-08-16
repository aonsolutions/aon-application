import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent implements OnInit {
  @Input() width                : string = '11.6rem';
  @Input() height               : string = '2.4rem';
  @Input() color                : string = '';
  @Input() backgroundColor      : string = '';
  @Input() border               : string = '';
  @Input() borderRadius         : string = '';
  @Input() padding              : string = '0.948rem';
  @Input() hoverBackgroundColor : string = '';
  @Input() hoverBorderColor     : string = '';
  @Input() hoverColor           : string = '';
  @Input() fontSize             : string = '';
  @Input() align                : string = 'center';
  @Input() class                : string = '';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';

  constructor() {
  }

  ngOnInit(): void {
    this.widthHost  = this.width;
    this.heightHost = this.height;
  }

}
