import { Component, Input, OnInit } from '@angular/core';
import { Message } from 'src/app/core/models/message';

@Component({
  selector: 'app-message-inbox',
  templateUrl: './message-inbox.component.html',
  styleUrls: ['./message-inbox.component.scss']
})
export class MessageInboxComponent implements OnInit {

  @Input() message: Message = new Message;
  @Input() detailView: boolean = false;
  tiles: number[] = [5,5,8,5,1];
  allTiles: boolean = true;

  constructor() {
  }

  ngOnInit(): void {
    if(this.message.type == 3){
      this.tiles = [5,5,14]
      this.allTiles = false;
    }
  }

  ngOnChange(): void {
    
  }

}
