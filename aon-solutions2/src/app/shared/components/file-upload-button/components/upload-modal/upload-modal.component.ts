import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FolderService } from 'src/app/core/services/folder.service';

interface Folder {
  value: string;
  text: string;
  disabled?: boolean;
}

@Component({
  selector: 'app-upload-modal',
  templateUrl: './upload-modal.component.html',
  styleUrls: ['./upload-modal.component.scss'],
})

export class UploadModalComponent implements OnInit {
  document: any;
  docName: string = '';
  folderList: Folder[] = [];
  folderSelected: string = '';
  folderNoSelected: boolean = false;
  fileAndFolder: any[] = [];

  currentDate = new Date()
    .toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
    .replace(/(\d+)\/(\d+)\/(\d+)/, '$1-$2-$3');

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<UploadModalComponent>,
    private folderService: FolderService
  ) {
    // Guardamos las carpetas en un array para mostrarlas en el select
    this.folderService.getFolderList().then((listFolders) => {
      listFolders.forEach((folder) => {
        this.folderList.push({
          value: folder.getKey(),
          text: folder.Name,
          disabled: folder.Name !== 'Contabilizado' ? true : false,
        });
      });
    });
  }

  ngOnInit(): void {}

  // Cerramos el modal
  closeModal(): void {
    this.dialogRef.close();
  }

  // Guardamos la carpeta seleccionada
  handleFolderSelected(event: any) {
    this.folderSelected = event;
  }

  // Comprobamos si se ha seleccionado una carpeta antes de subir el documento
  checkFolder(event: Event) {
    if (this.folderSelected === '') {
      event.preventDefault();
      this.folderNoSelected = true;
    } else {
      this.folderNoSelected = false;
    }
  }

  // Guardamos el documento y la carpeta seleccionada
  documentUpload(event: Event) {
    const target = event.target as HTMLInputElement;
    const file: File = (target.files as FileList)[0];
    this.document = file;
    this.docName = file.name;

    this.fileAndFolder.push({
      document: this.document,
      folder: this.folderSelected,
    });
  }

  // Limpiamos el documento subido
  clearDocument() {
    this.document = null;
    this.docName = '';
  }
}
