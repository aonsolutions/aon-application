import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CollectionFactory, Factory, ICollection, IMessage, StatusMessage, TypeMessage } from 'libraries/AonSDK/aon';
import { MessageService } from 'src/app/core/services/message.service';

@Component({
  selector: 'app-modal-edit-tax-model',
  templateUrl: './modal-edit-tax-model.component.html',
  styleUrls: ['./modal-edit-tax-model.component.scss'],
})
export class ModalEditTaxModelComponent implements OnInit {
  inputValue: string = '';
  newMessageDescription: string = '';
  entityFactory = new Factory();
  messagesData: IMessage = this.entityFactory.createMessage();
  collectionFactory = new CollectionFactory();
  messages: ICollection<IMessage> =
  this.collectionFactory.createMessageCollection();

  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
  }

  getValue(value: string): void {
    // this.message.Description = value;
  }
  closeModal(): void {
    this.dialogRef.close();
  }

  constructor(
    private messageService: MessageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalEditTaxModelComponent>
  ) {

  }
  async createMessage(description: string){
    if (this.messagesData) {
      const messageId = this.messagesData.Id;
      // const messageName = this.messagesData.Name;

      const newMessage: IMessage = {
        Id: messageId,
        Name: '',
        Title: '',
        Description: description,
        Date: new Date(),
        Status: StatusMessage.ABIERTA,
        Type: TypeMessage.CONSULTA,
        EndDate: new Date(),
        LastMessageChatOrigin: false,
        getKey: () => 'fakeKey',
        getFilterableFields: () => new Map(),
        getSortableFields: () => new Map(),
      };

      try {
        // Crear el mensaje
        const createdMessage =
          await this.messageService.createMessage(newMessage);

        // Agregar el nuevo mensaje
        this.messages.add(createdMessage);

      } catch (error) {
        console.error('Error al crear el mensaje de chat:', error);
      }
    }

  }

  sendMessage() {
    // para no enviar mensajes vacíos
    if (this.newMessageDescription.trim() === '') {
      return;
    }

    // Llama a la función para crear un nuevo mensaje
    this.createMessage(this.newMessageDescription);

    // Limpia el campo de entrada después de enviar el mensaje
    this.newMessageDescription = '';
  }

  ngOnInit(): void {}
}
