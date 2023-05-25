import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {

  @Input() padding: string = '';
  @Input() border: string = '';
  @Input() borderRadius: string = '.4rem';
  @Input() width: string = 'auto';
  @Input() height: string = 'auto';
  @Input() headerHeight: string = '0rem';
  @Input() footerHeight: string = '0rem';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';

  constructor() { }

  ngOnInit(): void {
    this.widthHost = this.width;
    this.heightHost = this.height;
  }

}
