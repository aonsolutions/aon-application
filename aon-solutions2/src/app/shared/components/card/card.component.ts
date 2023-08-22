import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector    : 'app-card',
  templateUrl : './card.component.html',
  styleUrls   : ['./card.component.scss']
})
export class CardComponent implements OnInit {
  @Input() width  : string = 'auto';
  @Input() height : string = 'auto';
  @Input() id     : string = '';
  @Input() class  : string = '';

  constructor() { }

  ngOnInit(): void {
  }

}
