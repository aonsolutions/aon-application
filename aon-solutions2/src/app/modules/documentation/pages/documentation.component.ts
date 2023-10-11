import { Component, OnInit, ViewChild } from '@angular/core';
import { DocumentService } from 'src/app/core/services/document.service';
import { FolderService } from 'src/app/core/services/folder.service';
import {
  CollectionFactory,
  Factory,
  FilterBuilder,
  ICollection,
  IDocument,
  IFolder,
} from 'libraries/AonSDK/src/aon';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { TranslateService } from '@ngx-translate/core';
import { UploadModalComponent } from 'src/app/shared/components/file-upload-button/components/upload-modal/upload-modal.component';
import { ErrorModalComponent } from 'src/app/shared/components/error-modal/error-modal.component';
import { RenameFileComponent } from '../components/modal-rename-file/rename-file.component';
import { ConfirmModalComponent } from 'src/app/shared/components/confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-documentation',
  templateUrl: './documentation.component.html',
  styleUrls: ['./documentation.component.scss'],
})
export class DocumentationComponent implements OnInit {
  @ViewChild('file') dropdownMenuComponent: DropdownMenuComponent =
    new DropdownMenuComponent();
  @ViewChild('modal') modalComponent: any = '';
  collectionFactory = new CollectionFactory();
  factory = new Factory();
  originalListFolders: ICollection<IFolder> =
    this.collectionFactory.createFolderCollection();
  documentationListFolders: ICollection<IFolder> =
    this.collectionFactory.createFolderCollection();
  documentationListSubFolders: ICollection<IFolder> =
    this.collectionFactory.createFolderCollection();
  originalDocListSubFolders: ICollection<IFolder> =
    this.collectionFactory.createFolderCollection();
  originalListDocuments: ICollection<IDocument> =
    this.collectionFactory.createDocumentCollection();
  documentsList: ICollection<IDocument> =
    this.collectionFactory.createDocumentCollection();
  fileToShow: IDocument = this.factory.createDocument();
  showMenu: boolean = false;
  selecFolder: string = '';
  selectedCards: string[] = [];
  showFiles: boolean = false;
  showNoElements: boolean = false;
  showDetail: boolean = false;
  showSelectedCount: boolean = false;
  showSelectAllBtn: boolean = true;
  menuItem: MenuItem[] = [];
  subMenuItemFolder: MenuItem[] = [];
  search: string = '';
  localePDF: string =
    this.translateService.getDefaultLang() === 'es' ? 'es-ES' : 'en-EN';

  folderSelected: string = '';
  idSelected: string = '';

  objectFactory = new Factory();
  document: IDocument = this.objectFactory.createDocument();

  constructor(
    private translateService: TranslateService,
    private documentService: DocumentService,
    private folderService: FolderService
  ) {
    /*
      Inicialmente solo devolvemos carpetas correspondientes a la rai�z
      es por ello que no pasamos parametro, para que por defecto sea ''
    */
    this.getDocumentation();
    /*
      Menu que tiene documento en <app-card>
    */
    // Carpetas principales
    let filter = new FilterBuilder();
    filter.addField('parent', '');
    this.folderService.getFolderList(filter.getFilter()).then((listFolders) => {
      listFolders.forEach((folder) => {
        this.subMenuItemFolder.push({
          root: true,
          text: folder.Name,
          icon: 'folder',
          click: () => this.getMoveFile(folder.Path),
        });
      });
    });
    // Menu con los datos traducidos
    this.translateService
      .get([
        'DOCUMENTATION.FILE_SELECT_PREVIEW',
        'DOCUMENTATION.FILE_SELECT_MOVE_TO',
        'DOCUMENTATION.FILE_SELECT_EDIT_NAME',
        'DOCUMENTATION.FILE_SELECT_LABEL_AS',
        'DOCUMENTATION.FILE_SELECT_DOWLOAD',
        'DOCUMENTATION.FILE_SELECT_DELETE',
      ])
      .subscribe((result) => {
        this.menuItem! = [
          {
            root: true,
            text: result['DOCUMENTATION.FILE_SELECT_PREVIEW'],
            icon: 'eye',
            colorIcon: 'black',
          },
          // {
          //   root: true,
          //   text: result['DOCUMENTATION.FILE_SELECT_MOVE_TO'],
          //   icon: 'folder_special',
          //   colorIcon: 'black',
          //   children: this.subMenuItemFolder,
          // },
          // {
          //   root: true,
          //   text: result['DOCUMENTATION.FILE_SELECT_EDIT_NAME'],
          //   icon: 'edit',
          //   colorIcon: 'black',
          // },
          // {
          //   root: true,
          //   text: result['DOCUMENTATION.FILE_SELECT_LABEL_AS'],
          //   icon: 'label',
          //   colorIcon: 'black',
          // },
          {
            root: true,
            text: result['DOCUMENTATION.FILE_SELECT_DOWLOAD'],
            icon: 'cloud_download',
            colorIcon: 'black',
            click: () => this.documentService.downloadDocument(this.fileToShow),
          },
          // {
          //   root: true,
          //   text: result['DOCUMENTATION.FILE_SELECT_DELETE'],
          //   icon: 'delete',
          //   colorIcon: 'black',
          // },
          //  {root:true, text: result['HEADER.LOGOUT']       , icon:'exit_to_app', colorIcon:'black', click:() => this.delete()},
        ];
      });
  }

  ngOnInit(): void {}

  // Acción que realiza al cerrar el modal
  // editName: any = (result: any) => this.renameFile(result, this);
  functionDocument: any = (result: any) => this.afterModalClosed(result);

  // Modal para subir el archivo
  uploadDocument(event: Event) {
    event.preventDefault();
    this.modalComponent.openDialog(
      UploadModalComponent,
      this.functionDocument,
      'Data from home'
    );
  }

  // Al cerrar el modal de subir documento se crea el documento en la base de datos
  afterModalClosed(result?: any) {
    // Si se ha seleccionado un documento
    if (result) {
      // Guardamos los datos del documento
      const fileName = result[0].document.name;
      const fileType = result[0].document.type;
      const fileSize = result[0].document.size;
      const path     = result[0].folder;

      // Creamos el documento
      this.document = this.objectFactory.createDocument(result[0].document, fileName, fileSize, fileType, new Date(), path);
      const file = result[0].document;

      // Subimos el documento
      this.documentService.uploadDocument(this.document, file)
      .then((response) => {
        this.uploadCompletedModal()
      })
      .catch((error) => {
        this.uploadErrorModal()
      })

    }
  }

  // Confirmación de subida de documento
  uploadCompletedModal() {
    this.modalComponent.openDialog(
      ConfirmModalComponent,
      this.functionDocument,
      'Documento subido correctamente'
    );
    this.getDocumentation(this.folderSelected, this.idSelected);
  }

  // Si la subida da error
  uploadErrorModal() {
    this.modalComponent.openDialog(
      ErrorModalComponent,
      this.functionDocument,
      'Error al subir el documento'
    );
  }

  /*
    Abrir modal para renombrar documento
  */
  // openModalRename(document: any) {
  //   this.modalComponent.openDialog(
  //     RenameFileComponent,
  //     this.editName,
  //     document.fileToShow
  //   );
  // }

  /*
    Descarga todos los documentos seleccionados
  */
  async downloadAllSelected() {
    for (const card of this.selectedCards) {
      const index: number = parseInt(card.split('-')[1]);
      const document = this.documentsList.toArray()[index];
      await this.documentService.downloadDocument(document);
    }
  }

  /*
  Devolvera:
  1 - Listado de carpetas si no se le pasa nada
  2 - Listado de subcarpetas si existiera de la carpeta pasada
  3 - Documentacion, si no existe subcarpetas de la carpeta pasada
  4 - No contiene ningun tipo de dato en la carpeta pasada
*/
  getDocumentation(folder: string = '', id: string = '') {
    this.search = '';
    this.documentationListFolders = this.originalListFolders;

    // Si la carpeta no está vacía guardamos los valores
    if (folder !== '') {
      this.folderSelected = folder;
      this.idSelected = id;
    }

    let filter = new FilterBuilder();
    filter.addField('parent', folder);

    this.search = '';
    this.documentationListFolders = this.originalListFolders;

    this.folderService.getFolderList(filter.getFilter()).then((listFolders) => {
      if (folder === '') {
        // 1 - Listado de carpetas
        // La primera vez que cargamos la vista no mostramos el menu lateral
        // con el listado de carpetas
        this.showMenu = false;
        this.originalListFolders = listFolders;
        this.documentationListFolders = listFolders;
      } else {
        // Si contiene sub carpetas
        const firstValue =
          Object.keys(listFolders).length > 0
            ? Object.values(listFolders)[0]
            : 0;
        if (firstValue.size > 0) {
          // 2 - Cargamos subcarpetas
          this.showFiles = false;
          this.showDetail = false;
          this.showNoElements = false;
          this.documentationListSubFolders = listFolders;
          this.originalDocListSubFolders = listFolders;
        } else {
          // 3 - No tiene sub carpetas, cargamos los documentos
          const filterDocument = new FilterBuilder();
          filterDocument.addField('path', folder);
          this.documentService
            .getDocumentList(filterDocument.getFilter())
            .then((documentsList) => {
              // 4 - Se mira si tenemos documentos o no, Si no tenemos:
              // this.showNoElements = true, para mostrar el mensaje correspondiente
              const firstValue =
                Object.keys(documentsList).length > 0
                  ? Object.values(documentsList)[0]
                  : 0;
              this.showNoElements = firstValue.size > 0 ? false : true;
              // Agregamos los datos devueltos
              this.originalListDocuments = documentsList;
              this.documentsList = documentsList;
            });
          this.showFiles = true;
        }

        if (!this.showMenu) {
          // Mostramos el menu
          this.showMenu = true;
          // Marcamos la carpeta seleccionada
          this.selecFolder = id;
        }
      }
    });
  }

  /*
    Devuelve el nombre con la extension (archivo.png)
  */
  getNameWithExtension(name: string, extension: string): string {
    const finalExtension = extension.split('/').pop();
    return name + '.' + finalExtension;
  }

  /*
    La carpeta que se esta visualizando
  */
  selectFolder(id: string) {
    //    console.log(this.selecFolder)
    //    console.log('select folder')

    // Eliminamos si existe otro marcado
    if (this.selecFolder === '') {
      this.selecFolder = '';
    }
    const elements = document.getElementsByClassName('select-folder');
    while (elements.length > 0) {
      elements[0].classList.remove('select-folder');
    }
    // Marcamos este
    let element = document.getElementById(id);
    element!.classList.add('select-folder');
  }

  /*
    Lee un documento para mostrar el previo
  */
  loadFile(file: IDocument, id: string) {
    // Eliminamos clase si existe un elemento ya marcado
    this.deleteViewFile();
    // Marcar como visiaualizado agregando clase
    let element = document.getElementById(id);
    element!.classList.add('select-view');
    // Cogemos el document
    this.showDetail = true;
    this.fileToShow = file;
    return file;
  }

  /*
    Cerrar el previo de la imagen
  */
  closeShowDocumentation() {
    // Eliminamos clase si existe un elemento ya marcado
    this.deleteViewFile();
    // Quitamos el visualizador
    this.showDetail = false;
  }

  /*
    Mover un documento a otra carpeta
    (seria uno varios)
  */
  getMoveFile(folder: string) {
    console.log(folder);
  }

  /*
    El documento que se esta visualizando
  */
  deleteViewFile() {
    // Eliminamos clase si existe un elemento ya marcado
    const elements = document.getElementsByClassName('select-view');
    while (elements.length > 0) {
      elements[0].classList.remove('select-view');
    }
  }

  selectViewFile(id: string, event: Event) {
    const documents_array = this.documentsList.toArray();
    const is_saved = this.selectedCards.includes(id);

    event.stopPropagation();

    let element = document.getElementById(id); // Cogemos el div principal del card

    // Verificamos si el card esta guardado. Si no lo esta se añade, de lo contrario lo quitamos.
    if (!is_saved) {
      this.selectedCards.push(id);
    } else {
      const indexCard = this.selectedCards.indexOf(id);
      this.selectedCards.splice(indexCard, 1);
    }

    const sel_cards_length = this.selectedCards.length;
    // Si es el último elemento, ocultamos botón de "Seleccionar todos"
    this.showSelectAllBtn = !(sel_cards_length === documents_array.length);

    // Si hay elementos seleccionados, muestra el mensaje de recuento, de lo contrario lo oculta.
    this.showSelectedCount = sel_cards_length > 0 ? true : false;

    // Si no existe la agregamos, si existe la removemos
    if (!element!.classList.contains('select')) {
      element!.classList.add('select');
    } else {
      element!.classList.remove('select');
    }
  }

  /*
    Renombrar un documento
  */
  // renameFile(result: string, document: any) {
  //   console.log(result);

  //   // Si el resultado no es undefined, es que se ha renombrado
  //   if (result !== undefined) {
  //     // Renombramos el documento en ambas listas
  //     this.documentsList.forEach((doc) => {
  //       if (doc.getKey() === document.fileToShow.getKey()) {
  //         doc.FileName = result;
  //       }
  //     });

  //     this.originalListDocuments.forEach((doc) => {
  //       if (doc.getKey() === document.fileToShow.getKey()) {
  //         doc.FileName = result;
  //       }
  //     });

  //     // Llamamos al servicios para actualizar la lista
  //     this.documentService
  //       .updateDocument(this.originalListDocuments)
  //       .then((result) => {
  //         console.log(result);
  //       });
  //   }
  // }

  /*
    Buscador de documentacion
  */
  searchDocumentation(search: string) {
    // Si estamos en la vista de archivos
    if (this.showMenu) {
      if (this.showFiles) {
        // Si no hay nada en el input, mostramos la lista original
        if (search === '') {
          this.documentsList = this.originalListDocuments;
          // Si hay algo en el input, filtramos la lista
        } else {
          this.documentsList =
            this.collectionFactory.createDocumentCollection();
          this.originalListDocuments.forEach((document) => {
            if (
              document.FileName.toLowerCase().includes(search.toLowerCase())
            ) {
              this.documentsList.add(document);
            }
          });
        }
      } else {
        if (search === '') {
          // Si no hay nada en el input, mostramos la lista original
          this.documentationListSubFolders = this.originalDocListSubFolders;
        } else {
          // Si hay algo en el input, filtramos la lista
          this.documentationListSubFolders =
            this.collectionFactory.createFolderCollection();
          this.originalDocListSubFolders.forEach((folder) => {
            if (folder.Name.toLowerCase().includes(search.toLowerCase())) {
              this.documentationListSubFolders.add(folder);
            }
          });
        }
      }
      // Si estamos en la vista de carpetas
    } else {
      if (search === '') {
        // Si no hay nada en el input, mostramos la lista original
        this.documentationListFolders = this.originalListFolders;
      } else {
        // Si hay algo en el input, filtramos la lista
        this.documentationListFolders =
          this.collectionFactory.createFolderCollection();

        this.originalListFolders.forEach((folder) => {
          if (folder.Name.toLowerCase().includes(search.toLowerCase())) {
            this.documentationListFolders.add(folder);
          }
        });
      }
    }
  }

  /**
   * Alterna la seleccion de todos los documentos en la lista.
   *
   * @return {void} La funcion no devuelve ningun valor.
   */
  toggleSelectAll() {
    const documentsArray = this.documentsList.toArray();
    const allSelected = this.selectedCards.length === documentsArray.length;

    documentsArray.forEach((card, i) => {
      // Seleccionamos todos los elementos que no estén seleccionados previamente
      if (!this.selectedCards.includes('file-' + i)) {
        document.getElementById('fileCheck-' + i)?.click();
      }
    });

    // Actualiza visibilidad de botón.
    this.showSelectAllBtn = allSelected;
    this.showSelectedCount = this.selectedCards.length > 0 ? true : false;
  }

  toggleDeselectAll() {
    const documentsArray = this.documentsList.toArray();
    const allSelected = this.selectedCards.length === documentsArray.length;
    documentsArray.forEach((card, i) => {
      // Deseleccionamos elementos
      if (allSelected && this.selectedCards.includes('file-' + i)) {
        document.getElementById('fileCheck-' + i)?.click();
      } else {
        // Recorremos los elementos seleccionados y los deseleccionamos
        this.selectedCards.forEach((card) => {
          let index = parseInt(card.split('-')[1]);
          document.getElementById('fileCheck-' + index)?.click()
        });
      }
    });
  }
}
