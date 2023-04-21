import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { Message } from 'src/app/core/models/message';

@Component({
  selector: 'app-detail-notification-task',
  templateUrl: './detail-notification-task.component.html',
  styleUrls: ['./detail-notification-task.component.scss']
})
export class DetailNotificationTaskComponent implements OnInit, OnChanges {

  @Input() message : Message = new Message();
  @Output() closed : any = new EventEmitter<any>();
  showForm: boolean = false;

  constructor() {
    this.showForm = false;
  }

  ngOnInit(): void {
    this.showForm = false;
  }

  ngOnChanges(): void {
    this.showForm = false;
  }

  showFormFunction(){
    this.showForm === true ? this.showForm = false : this.showForm = true;
  }

  close(){
    this.closed.emit('true');
  }

}

