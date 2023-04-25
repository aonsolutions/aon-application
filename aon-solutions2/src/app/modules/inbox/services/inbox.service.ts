import { Injectable } from '@angular/core';
import { Message } from 'src/app/core/models/message';
import { Asesor } from 'src/app/core/models/asesor';

@Injectable({
  providedIn: 'root'
})
export class InboxService {

  consultas : Message [] = [
    {type:1,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'dia, 00:00',status:1,lastMessageOrigin:true},
    {type:1,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'dia, 00:00',status:1,lastMessageOrigin:false},
    {type:1,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'dia, 00:00',status:0,lastMessageOrigin:false},
  ]
  tareas : Message [] = [
    {type:2,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'Vence mañana',status:1,lastMessageOrigin:true},
    {type:2,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'Vence en 1 día',status:1,lastMessageOrigin:false},
    {type:2,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'Vence en 2 semanas',status:0,lastMessageOrigin:false},
  ]
  notificaciones : Message [] = [
    {type:3,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'fecha',status:1,lastMessageOrigin:false},
    {type:3,name:'Nombre',subject:'Asunto',description:'Descripción del mensaje',date:'fecha',status:0,lastMessageOrigin:false},
  ]
  asesores: Asesor [] = [
    {id:1,name:'Nombre Apellidos',type:'Asesor fiscal'},
    {id:2,name:'Nombre Apellidos',type:'Asesor contable'},
    {id:3,name:'Nombre Apellidos',type:'Asesor laboral'},
    {id:4,name:'Nombre Apellidos',type:'Asesor jurídico'}
  ]

  todas: Message [] = this.consultas.concat(this.tareas).concat(this.notificaciones);

  constructor() { }

  getMessages(typeMessage:string,statusMessage:number){
    switch(typeMessage){
      case 'bandeja':{
        return this.todas;
        break;
      }
      case 'consultas':{
        return this.consultas;
        break;
      }
      case 'tareas':{
        return this.tareas;
        break;
      }
      case 'notificaciones':{
        return this.notificaciones;
        break;
      }
    }
    return [];
  }

  getAllMessagesFromConsult(){
    return this.consultas;
  }

  getAsesores(){
    return this.asesores;
  }

}
