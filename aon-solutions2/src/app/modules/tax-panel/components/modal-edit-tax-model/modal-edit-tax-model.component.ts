import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, Factory, ICollection, IMessage, StatusMessage, TypeMessage, } from 'libraries/AonSDK/src/aon';
import { MessageService } from 'src/app/core/services/message.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

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
  model: any;
  spinner: boolean = false;

  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
  }

  closeModal(): void {
    this.dialogRef.close();
  }

  constructor(
    private taxModelService: TaxModelService,
    private messageService: MessageService,
    private translateService: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalEditTaxModelComponent>
  ) {
    this.taxModelService.getTax(data.key).then((response) => {
      this.model = response;
    });
  }

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
              this.translateService.get('TAX_PANEL.OF').subscribe((of) => {
                title = `${rectifyingModel} ${this.model.Name}, ${trimester} ${this.model.Trimester}, ${of} ${this.model.Year}`;
              });
            });
        });

      const newMessage: IMessage =
        this.messageService.objectFactory.createMessage();
      newMessage.Id = messageId;
      newMessage.Name = '';
      newMessage.Title = title;
      newMessage.Description = description;
      newMessage.Date = new Date();
      newMessage.Status = StatusMessage.ABIERTA;
      newMessage.Type = TypeMessage.CONSULTA;
      newMessage.EndDate = new Date();
      newMessage.LastMessageChatOrigin = false;

      // Crear el mensaje
      const createdMessage = await this.messageService.createMessage(
        newMessage
      );

        // Ocultar el spinner después de crear el mensaje
        this.spinner = false;
        this.dialogRef.close();

    }
  }

  sendMessage() {
    // para no enviar mensajes vacíos
    if (this.newMessageDescription.trim() === '') {
      return;
    }

    this.spinner = true;

    // Llama a la función para crear un nuevo mensaje
    this.createMessage(this.newMessageDescription);

    // Limpia el campo de entrada después de enviar el mensaje
    this.newMessageDescription = '';
  }

  ngOnInit(): void {}
}
