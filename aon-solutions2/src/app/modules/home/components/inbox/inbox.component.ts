import { Component, OnInit } from '@angular/core';
//NOTIFICACION TAREA Y CONSULTA
export interface Message{
  type : number;
  name : string;
  subject : string;
  date : string;
  description : string;
}
 export interface NotificationType{

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
    {name : "NOMBRE", type : 1, subject : "asunto", date:"dia,00:00", description:"Descripción del mensaje"},
    {name : "NOMBRE", type : 2,subject : "asunto",date:"dia,00:00", description:"Descripción del mensaje"},
    {name : "NOMBRE", type : 3,subject : "asunto",date:"dia,00:00", description:"Descripción del mensaje"},
    {name : "NOMBRE", type : 2,subject : "asunto",date:"dia,00:00", description:"Descripción del mensaje"}
  ]
  constructor() { }

  ngOnInit(): void {
  }
}
