import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-pagination-container',
  templateUrl: './pagination-container.component.html',
  styleUrls: ['./pagination-container.component.scss']
})
export class PaginationContainerComponent implements OnInit {
  
  @Input() width : string = 'auto';
  @Input() height : string = 'auto';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';

  constructor() { }

  ngOnInit(): void {
    this.widthHost = this.width;
    this.heightHost = this.height;
  }

}
