import { Component, Inject, Input, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FolderService } from 'src/app/core/services/folder.service';

interface Folder {
  value: string;
  text: string;
}

@Component({
  selector    : 'app-upload-modal',
  templateUrl : './upload-modal.component.html',
  styleUrls   : ['./upload-modal.component.scss'],
})
export class UploadModalComponent implements OnInit {
  document        : any;
  docName         : string = '';
  folderList      : Folder[] = [];
  folderSelected  : string = '/a_contabilizar';
  folderNoSelected: boolean = false;
  fileAndFolder   : any[] = [];

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
  // handleFolderSelected(event: any) {
  //   this.folderSelected = event;
  // }

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
  async previewDocumentsUpload(event: any) {
    const files =  event;
    
    Array.from(files).forEach(file => { 
      this.document = file;
      this.docName  = this.document['name'];
      
      this.fileAndFolder.push({
        document: this.document,
        folder  : this.folderSelected,
      });
    });
  }

  // Limpiamos el documento subido
  clearDocument() {
    this.document = null;
    this.docName = '';
  }
  
  /*
  // Acción que realiza al cerrar el modal
  functionHome: any = (result: any) => this.afterModalClosed(result);

  // Función que se ejecuta al cerrar el modal
  afterModalClosed(result: any) {
    // Si se ha seleccionado un documento
    if (result) {
      // Guardamos los datos del documento
      const fileName = result[0].document.name;
      const fileType = result[0].document.type;
      const fileSize = result[0].document.size;
      const path = result[0].folder;

      // Creamos el documento
      this.document = this.objectFactory.createDocument(
        result[0].document,
        fileName,
        fileSize,
        fileType,
        new Date(),
        path
      );
      const file = result[0].document;

      // Subimos el documento
      this.documentService
        .uploadDocument(this.document, file)
        .then((response) => {
          this.uploadCompletedModal();
        })
        .catch((error) => {
          this.uploadErrorModal();
        });
    }
  }

  // Confirmación de subida de documento
  uploadCompletedModal() {
    this.snackBar.openFromComponent(ResultSnackBarComponent, {
      data: {
        message: 'Documento subido correctamente',
        icon: 'check_circle',
        preClose: () => {
          this.snackBar.dismiss();
        },
      },
      panelClass: ['correct-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 3000,
    });
  }

  // Si la subida da error
  uploadErrorModal() {
    this.snackBar.openFromComponent(ResultSnackBarComponent, {
      data: {
        message: 'Error al subir el documento',
        icon: 'error',
        preClose: () => {
          this.snackBar.dismiss();
        },
      },
      panelClass: ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 3000,
    });
  }
*/
}
