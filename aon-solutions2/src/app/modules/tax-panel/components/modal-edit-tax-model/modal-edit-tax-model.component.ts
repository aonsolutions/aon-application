import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import {
  CollectionFactory,
  ErrorResponse,
  Factory,
  ICollection,
  IMessage,
  StatusMessage,
  TypeMessage,
} from 'libraries/AonSDK/src/aon';
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

  dataSeparator: string = ';';
  dataParts: string[] = [];

  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
  }

  closeModal(): void {
    this.dialogRef.close();
  }

  constructor(
    private messageService: MessageService,
    private translateService: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalEditTaxModelComponent>
  ) {}

  async createMessage(description: string) {
    if (this.messagesData) {
      const messageId = this.messagesData.Id;

      let title = '';
      this.translateService
        .get('TAX_PANEL.RECTIFYING_MODEL')
        .subscribe((rectifyingModel) => {
          this.translateService
            .get('TAX_PANEL.TRIMESTER')
            .subscribe((trimester) => {
              this.translateService
                .get('TAX_PANEL.OF')
                .subscribe((of) => {
                  title = `${rectifyingModel} ${this.dataParts[0]}, ${trimester} ${this.dataParts[1]}, ${of} ${this.dataParts[2]}`;
                  console.log(title);
                });
            });
        });

      // const newMessage: IMessage = {
      //   Id: messageId,
      //   Name: '',
      //   Title: title,
      //   Description: description,
      //   Date: new Date(),
      //   Status: StatusMessage.ABIERTA,
      //   Type: TypeMessage.CONSULTA,
      //   EndDate: new Date(),
      //   LastMessageChatOrigin: false,
      //   getKey: () => messageId,
      //   getFilterableFields: () => new Map(),
      //   getSortableFields: () => new Map(),
      // };

      try {
        // Crear el mensaje
        // const createdMessage = await this.messageService.createMessage(
        //    newMessage
        // );

        // Agregar el nuevo mensaje
        // this.messages.add(createdMessage);
      } catch (error) {
        throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
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

  ngOnInit(): void {
    this.dataParts = this.data['key'].split(this.dataSeparator);
    console.log(this.dataParts);
  }
}
