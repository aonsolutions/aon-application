import { Component, OnInit } from '@angular/core';

export interface Message{
  type : number;
  name : string;
  subject : string;
  date : string;
  description : string;
}
@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class InboxComponent implements OnInit {

  messages : Message[] = [
    {name : "Nombre ", type : 1, subject : "Asunto", date:"dia,00:00", description:"Descripción del mensaje"},
    {name : "Nombre ", type : 2,subject : "Asunto",date:"dia,00:00", description:"Descripción del mensaje"},
    {name : "Nombre ", type : 3,subject : "Asunto",date:"dia,00:00", description:"Descripción del mensaje"},
    {name : "Nombre ", type : 2,subject : "Asunto",date:"dia,00:00", description:"Descripción del mensaje"}
  ]
  constructor() { }

  ngOnInit(): void {
  }
}
