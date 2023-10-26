import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector    : 'app-card',
  templateUrl : './card.component.html',
  styleUrls   : ['./card.component.scss']
})
export class CardComponent implements OnInit {
  @Input() id             : string = '';
  @Input() class          : string = '';
  @Input() cardMatTooltip : any = '';

  constructor() { }

  ngOnInit(): void {
  }

}
