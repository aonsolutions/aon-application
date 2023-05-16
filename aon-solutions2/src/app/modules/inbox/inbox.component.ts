import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {

  bandejaClass: string = 'optionMenuInbox';
  consultasClass: string = 'optionMenuInbox';
  tareasClass: string = 'optionMenuInbox';
  notificacionesclass: string = 'optionMenuInbox';
  class: string = 'optionMenuInbox';
  onClickClass: string = 'optionMenuInbox optionMenuInboxClicked';
  selectedMenu : string = "";
  menuColor: string = 'white'
  menuCircleColor: string = '#feedec'

  constructor() {
    this.selectedMenu = 'bandeja';
    this.bandejaClass = this.onClickClass;
  }

  resetClass(){
    this.consultasClass = this.bandejaClass = this.tareasClass = this.notificacionesclass = this.class;
  }

  ngOnInit(): void {
  }

}
