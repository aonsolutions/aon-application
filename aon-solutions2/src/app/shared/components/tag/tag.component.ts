import { Component, HostBinding, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-tag',
  templateUrl: './tag.component.html',
  styleUrls: ['./tag.component.scss'],
  host:{
    'style.width': 'auto',
    'style.height': 'auto'
  }
})
export class TagComponent implements OnInit {

  @ViewChild('tag') tag: any = '';
  @Input() color: string = '';
  @Input() backgroundColor: string = '';
  @Input() padding: string = '';
  @Input() border: string = '';
  @Input() borderRadius: string = '';
  @Input() width: string = 'fit-content';
  @Input() height: string = 'fit-content';
  @Input() maxValue: number = 0;
  @Input() fontSize: string = '.8rem';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';

  constructor() {
    this.widthHost = this.width;
    this.heightHost = this.height;
  }

  ngOnInit(): void { 
  }

  ngAfterViewInit()	{
    if(this.maxValue != 0){
      try {
        let number = +this.tag.nativeElement.innerHTML;
        if(number > this.maxValue){
          this.tag.nativeElement.innerHTML = '+'+this.maxValue;
        }
      } catch (error) {
        throw('Can not convert string to type number');
      }
    }
  }

}
