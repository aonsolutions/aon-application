import { Component, EventEmitter, Inject, Output, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ResultSnackBarComponent } from 'src/app/shared/components/result-snack-bar/result-snack-bar.component';
import { FolderService } from 'src/app/core/services/folder.service';

interface Folder {
  value : string;
  text  : string;
}

@Component({
  selector    : 'app-upload-modal',
  templateUrl : './upload-modal.component.html',
  styleUrls   : ['./upload-modal.component.scss'],
})

export class UploadModalComponent implements OnInit {
  @Output() getUploadingdFiles: EventEmitter<any> = new EventEmitter();
  isDropOver      : boolean   = false;
  folderList      : Folder[]  = [];
  folderSelected  : string    = '/a_contabilizar';
  folderNoSelected: boolean   = false;
  filesAndFolder  : any[]     = [];
  currentDate = new Date()
    .toLocaleDateString('es-ES', {
      year  : 'numeric',
      month : '2-digit',
      day   : '2-digit'
    })
    .replace(/(\d+)\/(\d+)\/(\d+)/, '$1-$2-$3');


  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private folderService   : FolderService,
    private snackBar        : MatSnackBar
  ) {
    // Guardamos las carpetas en un array para mostrarlas en el select
    this.folderService.getFolderList().then((listFolders) => {
      listFolders.forEach((folder) => {
        this.folderList.push({
          value : folder.getKey(),
          text  : folder.Name,
        });
      });
    });
  }

  ngOnInit(): void {
    
  }

/*
  Evento cogiendo documentos
*/
  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.previewDocumentsUpload(target.files);
    }
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer && event.dataTransfer.files) {
      this.previewDocumentsUpload(event.dataTransfer.files);
    }
    this.isDropOver = false;
  }

  onDragOver(event: any) {
    event.preventDefault();
    this.isDropOver = true;
  }

  onDragLeave(event: any) {
    event.preventDefault();
    this.isDropOver = false;
  }

/*
  FIN - Evento cogiendo documentos
*/

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
    // Mirar si tenemos carpeta marcada
    if(!this.folderNoSelected){
      const files = event;
      Array.from(files).forEach((file) => {
        // Montamos los datos a enviar, cuando se cierre el modal
        this.filesAndFolder.push({
          document: file,
          path    : this.folderSelected,
          uploaded: 'uploaded',
          error   : ''
        });
      });
    } else {
      const err = {result: 'Selecciona una carpeta'};
      this.uploadErrorModal(err);
    }
  }

  // Limpiamos el documento subido
  clearDocument(index: number) {
    // Quitamos el documento escogido
    this.filesAndFolder.splice(index, 1);
  }
  
  // Si la subida da error
  uploadErrorModal(err: any) {
    this.snackBar.openFromComponent(ResultSnackBarComponent, {
      data: {
        message: err.result,
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
