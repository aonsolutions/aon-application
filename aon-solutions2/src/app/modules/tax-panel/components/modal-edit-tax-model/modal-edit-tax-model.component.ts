import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IMessage } from 'libraries/AonSDK/aon';
import { MessageService } from 'src/app/core/services/message.service';

@Component({
  selector: 'app-modal-edit-tax-model',
  templateUrl: './modal-edit-tax-model.component.html',
  styleUrls: ['./modal-edit-tax-model.component.scss'],
})
export class ModalEditTaxModelComponent implements OnInit {
  inputValue: string = '';

  getValue(value: string): void {
    this.inputValue = value;
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
    // const newMessage: IMessage = {
    //   Description: this.inputValue,
    // };

    // this.inputValue !== '' && this.messageService.createMessage(newMessage).then((messageCreated) => {

    //   console.log('Mensaje creado:', messageCreated);
    //   this.closeModal();
    // });
  }


  ngOnInit(): void {}
}
