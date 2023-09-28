import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Factory, IMessage } from 'libraries/AonSDK/aon';
import { MessageService } from 'src/app/core/services/message.service';

@Component({
  selector: 'app-modal-edit-tax-model',
  templateUrl: './modal-edit-tax-model.component.html',
  styleUrls: ['./modal-edit-tax-model.component.scss'],
})
export class ModalEditTaxModelComponent implements OnInit {
  inputValue: string = '';

  // let factory = new Factory();

  // let messageTest: IMessage = factory.createMessage();

  // message: IMessage = {
  //   Id: '',
  //   Name: '',
  //   Title: '',
  //   Description: '',
  //   Date: new Date(),
  //   Type: 'consulta',
  //   Status: 'abierta',
  //   EndDate: new Date()
  //   // getKey: () => this.message.Id;
  //   // getFilterableFields: () => string[],
  //   // getSortableFields: () => string[],;
  // };

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
  createMessage(): void {
    // this.messageService.createMessage(this.message)
    //   .then((createdMessage: IMessage) => {


    //     console.log('Mensaje creado:', createdMessage);
    //     // Reinicia el valor del input
    //     this.message.Description = '';
    //   })
    //   .catch((error: any) => {
    //     // Maneja el error
    //     console.error('Error al crear el mensaje:', error);
    //   });
  }


  ngOnInit(): void {}
}
