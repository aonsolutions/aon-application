import { Component, EventEmitter, HostBinding, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UploadModalComponent } from './components/upload-modal/upload-modal.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Factory } from 'libraries/AonSDK/src/aon';
import { ResultSnackBarComponent } from 'src/app/shared/components/result-snack-bar/result-snack-bar.component';
import { DocumentService } from 'src/app/core/services/document.service';

@Component({
  selector    : 'app-file-upload-button',
  templateUrl : './file-upload-button.component.html',
  styleUrls   : ['./file-upload-button.component.scss'],
})

export class FileUploadButtonComponent implements OnInit {
  objectFactory = new Factory();
  @Output() getUploadedFiles: EventEmitter<FileList> = new EventEmitter();
  @ViewChild('modalUploadFile') modalUploadFileComponent: any = '';
  functionUploadFile: any = (result:any) => this.afterModalUploadFileClosed(result);
  @Input() isOpenUploadModal: boolean = false;
  width                     : string  = '100%';
  height                    : string  = '100%';
  isDropOver                : boolean = false;
  filesAndFolderUploaded    : any[]     = [];
  filesAndFolderUploadedView: boolean = true;
  
  constructor(
    private snackBar: MatSnackBar,
    private documentService : DocumentService,
  ) {
   
  }

  ngOnInit(): void {}

  // Modal para subir el archivo
  modalUploadDocument(event: Event) {
    if(!this.isOpenUploadModal){
      event.preventDefault();
      this.modalUploadFileComponent.openDialog(
        UploadModalComponent,
        this.functionUploadFile
      );
    }
  }

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.getUploadedFiles.emit(target.files);
    }
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer && event.dataTransfer.files) {
      this.getUploadedFiles.emit(event.dataTransfer.files);
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
  Subir documento
*/
  afterModalUploadFileClosed(result?: any){
    if(result !== undefined && result.length > 0)
      this.uploadDocument(result)
  }
  // Guardamos los documentos y la carpeta seleccionada
  uploadDocument(filesAndFolder: any) {
    //Si se ha seleccionado un documento
    if (filesAndFolder.length > 0) {
      this.filesAndFolderUploadedView = true;
      this.filesAndFolderUploaded     = filesAndFolder;
      // Guardamos el documento en la carpeta seleccionada
      Object.keys(filesAndFolder).forEach(key => {
        const file = filesAndFolder[key];
        // Montamos el documento, para enviar
          const document = this.objectFactory.createDocument(
            "",
            file.document.name,
            file.document.size,
            file.document.type,
            new Date(),
            file.path
          );
        // FIN Montamos el documento, para enviar
        // Subimos el documento
          this.documentService.uploadDocument(document, file.document).then((response) => {
            if(response)
              filesAndFolder[key].uploaded = 'up';
            else
              filesAndFolder[key].uploaded = 'error';
//            this.uploadCompletedModal();
          }).catch((err) => {
            filesAndFolder[key].uploaded  = 'error';
            filesAndFolder[key].error     = err.result;
            console.log('Error: ', err);
//            this.uploadErrorModal(err);
          });
        // FIN Subimos el documento
      });
    }
  }

/*  
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
*/
/*
  FIN - Subir documento
*/
/*
  Cerrar subir documento
*/
  closeUploadingFiles(){
    this.filesAndFolderUploadedView = false;
  }
/*
  FIN subir documento
*/

}
