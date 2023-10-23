import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-confirm-query',
  templateUrl: './modal-confirm-query.component.html',
  styleUrls: ['./modal-confirm-query.component.scss']
})
export class ModalConfirmQueryComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalConfirmQueryComponent>,
  ) { }

  ngOnInit(): void {
  }

  closeModal(): void {
  this.dialogRef.close();
  window.location.reload();
  }

  // Llama a la función para crear un nuevo mensaje
  // sendMessage(): void {
  //   if (typeof this.data.sendMessage === 'function') {
  //     this.data.sendMessage();
  //     this.closeModal();
  //   }
  //   window.location.reload();
  // }

}
