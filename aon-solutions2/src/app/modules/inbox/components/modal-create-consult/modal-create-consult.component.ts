import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { InboxService } from '../../services/inbox.service';
import { Asesor } from 'src/app/core/models/asesor';

@Component({
  selector: 'app-modal-create-consult',
  templateUrl: './modal-create-consult.component.html',
  styleUrls: ['./modal-create-consult.component.scss']
})
export class ModalCreateConsultComponent implements OnInit {

  position: boolean = false;
  private readonly _matDialogRef: MatDialogRef<ModalCreateConsultComponent>;
  asesores: Asesor [] = [];
  selectedAsesor: Asesor = new Asesor;
  subject: string = '';
  description: string = '';

  constructor(public dialogRef: MatDialogRef<ModalCreateConsultComponent>, @Inject(MAT_DIALOG_DATA) public data: any, inboxService: InboxService) {
    this._matDialogRef = dialogRef;
    this.asesores = inboxService.getAsesores();
    if(this.asesores.length > 0)
      this.selectedAsesor = this.asesores[0];
  };

  onNoClick(): void {
    this.dialogRef.close();
  }

  changePosition(){
    if(this.position){
      const matDialogConfig: MatDialogConfig = new MatDialogConfig();
      matDialogConfig.width = '48.7rem';
      matDialogConfig.height = '43rem';
      this._matDialogRef.updateSize(matDialogConfig.width, matDialogConfig.height);
      matDialogConfig.position = { right: '1rem', bottom: '0px' };
      this._matDialogRef.updatePosition(matDialogConfig.position);
      this.position = false;
    } else {
      const matDialogConfig: MatDialogConfig = new MatDialogConfig();
      matDialogConfig.width = 'calc(100% - 19.625rem)';
      matDialogConfig.height = 'calc(100% - 4rem)';
      this._matDialogRef.updateSize(matDialogConfig.width, matDialogConfig.height);
      matDialogConfig.position = { right: '0px', bottom: '0px' };
      this._matDialogRef.updatePosition(matDialogConfig.position);
      this.position = true;
    }
  }

  ngOnInit(): void {
    const matDialogConfig: MatDialogConfig = new MatDialogConfig();
    matDialogConfig.maxWidth = '100%';
    matDialogConfig.position = { right: '1rem', bottom: '0px' };
    matDialogConfig.width = '48.7rem';
    matDialogConfig.height = '43rem';
    this._matDialogRef.updateSize(matDialogConfig.maxWidth);
    this._matDialogRef.updateSize(matDialogConfig.width, matDialogConfig.height);
    this._matDialogRef.updatePosition(matDialogConfig.position);
    this._matDialogRef.disableClose = true;
  }

  createConsult(){
    console.log(this.selectedAsesor);
    console.log(this.subject);
    console.log(this.description);
  }

}
