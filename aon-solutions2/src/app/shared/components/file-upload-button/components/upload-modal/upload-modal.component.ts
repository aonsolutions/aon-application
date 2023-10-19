import { Component, Inject, Input, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FolderService } from 'src/app/core/services/folder.service';
import { ResultSnackBarComponent } from '../../../result-snack-bar/result-snack-bar.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DocumentService } from '../../../../../core/services/document.service';
import { Factory } from 'libraries/AonSDK/src/aon';
import { documents } from '../../../../../../../libraries/AonSDK/src/models/Document';

interface Folder {
  value: string;
  text: string;
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
  folderSelected: string = '/a_contabilizar';
  folderNoSelected: boolean = false;
  fileAndFolder: any[] = [];
  isOpenUploadModal: boolean = true;
  currentDate = new Date()
    .toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
    .replace(/(\d+)\/(\d+)\/(\d+)/, '$1-$2-$3');

  objectFactory = new Factory();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<UploadModalComponent>,
    private folderService: FolderService,
    private documentService: DocumentService,
    private snackBar: MatSnackBar
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
    const files = event;

    Array.from(files).forEach((file) => {
      this.document = file;
      this.docName = this.document['name'];
      this.fileAndFolder.push({
        document: this.document,
        folder: this.folderSelected,
      });
    });
  }

  // Limpiamos el documento subido
  clearDocument() {
    this.fileAndFolder = [];
    this.document = null;
    this.docName = '';
  }

  // Guardamos los documentos y la carpeta seleccionada
  uploadDocument() {
    //Si se ha seleccionado un documento
    if (this.fileAndFolder.length > 0) {
      // Guardamos el documento en la carpeta seleccionada
      this.fileAndFolder.forEach((file) => {
        console.log(file.document);
        console.log(file.folder);
        this.documentService.uploadDocument(file.folder, file.document)
        .then(() => {
          this.uploadCompletedModal();
        })
        .catch((err) => {
          console.log('Error: ', err);
          this.uploadErrorModal();
        });
      });
      this.closeModal();
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
}
