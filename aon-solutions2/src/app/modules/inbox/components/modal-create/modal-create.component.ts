import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CollectionFactory, Factory, ICollection, IMessage, StatusMessage, TypeMessage } from 'libraries/AonSDK/src/aon';
import { MessageService } from 'src/app/core/services/message.service';

export interface Holders {
  Id: string;
  Name: string;
}

@Component({
  selector: 'app-create-query',
  templateUrl: './modal-create.component.html',
  styleUrls: ['./modal-create.component.scss']
})
export class ModalCreateComponent implements OnInit {
  @Output() consultaCreated: EventEmitter<void> = new EventEmitter<void>();
  holders: any;
  advisors: any = "";
  advisorsList: any[] = [];
  selectedAdvisor: string | undefined;
  spinner: boolean = true;
  inputValue: string = '';
  newMessageDescription: string = '';
  newMessageAsunto: string = '';
  entityFactory = new Factory();
  messagesData: IMessage = this.entityFactory.createMessage();
  collectionFactory = new CollectionFactory();
  messages: ICollection<IMessage> =
  this.collectionFactory.createMessageCollection();

  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
    this.newMessageAsunto = newValue;
  }
  constructor(
    private messageService: MessageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalCreateComponent>,
  ) {
     messageService.getTaskHoldersList().then((response) => {
      this.holders = response;
      this.advisorsList = this.holders.toArray().map((element: Holders) =>({
        value: element.Name,
        text: element.Name
      }))
     })
    }

  ngOnInit(): void {
  }

  closeModal(): void {
    this.dialogRef.close();
  }

  // Cargar los asesores
  loadAdvisors(event: any) {
    this.selectedAdvisor = event;
    if (this.selectedAdvisor) {
      this.messagesData.Name = this.selectedAdvisor;
      this.messagesData.Title = 'Sin titulo';
    }
  }

  // Crear un mensaje tipo consulta
  async createMessage(description: string, asunto: string){
    if (this.messagesData) {

      const newMessage = this.messageService.objectFactory.createMessage()
      .setName(this.messagesData.Name)
      .setDescription(description)
      .setType(TypeMessage.CONSULTA)
      .setStatus(StatusMessage.CERRADA)
      .setTitle(asunto)
      .setLastMessageChatOrigin(false);

        // Crear el mensaje
        await this.messageService.createMessage(newMessage).then((response) => {
          this.messagesData = response;
          // Desactivo el spinner
          this.spinner = false;

        })
        // Agregar el nuevo mensaje
        this.messages.add(this.messagesData);
        window.location.reload();
    }
  }

  // Enviar la consulta creada
  sendMessage() {
    // para no enviar mensajes vacíos
    if (this.newMessageDescription.trim() === '') {
      return;
    }

    // Llama a la función para crear un nuevo mensaje
    this.createMessage(this.newMessageDescription, this.newMessageAsunto);

    // Limpia el campo de entrada después de enviar el mensaje
    this.newMessageDescription = '';
    this.newMessageAsunto = '';
  }
}
