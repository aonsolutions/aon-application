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
  @Input() tiles: number[] = [];
  @Input() allTiles: boolean = true;
  @Input() selectedMenu: string = ''

  constructor() {
  }

  ngOnInit(): void {
        
  }

  ngOnChange(): void {
    
  }

}
