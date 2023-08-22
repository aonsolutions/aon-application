import { Component, OnInit, ViewChild } from '@angular/core';
import { DocumentService } from 'src/app/core/services/document.service';
import { FolderService } from 'src/app/core/services/folder.service';
import { CollectionFactory, Factory, FilterBuilder, ICollection, IDocument, IFolder } from 'libraries/AonSDK/aon';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { DropdownMenuComponent } from '../../../shared/components/dropdown-menu/dropdown-menu.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector    : 'app-documentation',
  templateUrl : './documentation.component.html',
  styleUrls   : ['./documentation.component.scss']
})
export class DocumentationComponent implements OnInit {
  @ViewChild('file') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  collectionFactory = new CollectionFactory();
  factory           = new Factory();
  documentationListFolders    : ICollection<IFolder>    = this.collectionFactory.createFolderCollection();
  documentationListSubFolders : ICollection<IFolder>    = this.collectionFactory.createFolderCollection();
  documentsList               : ICollection<IDocument>  = this.collectionFactory.createDocumentCollection();
  fileToShow        : IDocument   = this.factory.createDocument();
  showMenu          : boolean     = false;
  selecFolder       : string      = '';
  showFiles         : boolean     = false;
  showNoElements    : boolean     = false;
  showDetail        : boolean     = false;
  menuItem          : MenuItem [] = []
  subMenuItemFolder : MenuItem [] = []
  
  constructor(
    private translateService: TranslateService,
    private documentService : DocumentService, 
    private folderService   : FolderService)
  {
    /*
      Inicialmente solo devolvemos carpetas correspondientes a la rai­z
      es por ello que no pasamos parametro, para que por defecto sea ''
    */
      this.getDocumentation();
    /*
      Menu que tiene documento en <app-card>
    */
      // Carpetas principales
      let filter    = new FilterBuilder();
      filter.addField('parent', '');
      this.folderService.getFolderList(filter.getFilter()).then(listFolders => {
        listFolders.forEach(folder => {
          this.subMenuItemFolder.push(
            {root: true, text: folder.Name, click:() => this.getMoveFile(folder.Path)},
          );
        });
      })
      // Menu con los datos traducidos
      this.translateService.get(
        [
          "DOCUMENTATION.FILE_SELECT_PREVIEW", "DOCUMENTATION.FILE_SELECT_MOVE_TO", 
          "DOCUMENTATION.FILE_SELECT_EDIT_NAME", "DOCUMENTATION.FILE_SELECT_LABEL_AS", 
          "DOCUMENTATION.FILE_SELECT_DOWLOAD", "DOCUMENTATION.FILE_SELECT_DELETE",
        ]
      ).subscribe( result => {
        this.menuItem! = [
          {root: true, text: result["DOCUMENTATION.FILE_SELECT_PREVIEW"]  , icon:'eye'            , colorIcon:'black'},
          {root: true, text: result["DOCUMENTATION.FILE_SELECT_MOVE_TO"]  , icon:'folder_special' , colorIcon:'black', children: this.subMenuItemFolder},
          {root: true, text: result["DOCUMENTATION.FILE_SELECT_EDIT_NAME"], icon:'edit'           , colorIcon:'black'},
          {root: true, text: result["DOCUMENTATION.FILE_SELECT_LABEL_AS"] , icon:'label'          , colorIcon:'black'},
          {root: true, text: result["DOCUMENTATION.FILE_SELECT_DOWLOAD"]  , icon:'cloud_download' , colorIcon:'black'},
          {root: true, text: result["DOCUMENTATION.FILE_SELECT_DELETE"]   , icon:'delete' , colorIcon:'black'},
        //  {root:true, text: result['HEADER.LOGOUT']       , icon:'exit_to_app', colorIcon:'black', click:() => this.delete()},
        ];
      });
  }

  ngOnInit(): void {
  }

/*
  Devolvera:
  1 - Listado de carpetas si no se le pasa nada
  2 - Listado de subcarpetas si existiera de la carpeta pasada
  3 - Documentacion, si no existe subcarpetas de la carpeta pasada
  4 - No contiene ningun tipo de dato en la carpeta pasada
*/
  getDocumentation(folder: string = '', id: string = '') {
    let filter    = new FilterBuilder();
    filter.addField('parent', folder);
    
    this.folderService.getFolderList(filter.getFilter()).then(listFolders => {
      if (folder === '') {
        // 1 - Listado de carpetas
        // La primera vez que cargamos la vista no mostramos el menu lateral 
        // con el listado de carpetas
        this.showMenu                 = false;
        this.documentationListFolders = listFolders;
      } else {
        // Si contiene sub carpetas
        const firstValue    = Object.keys(listFolders).length > 0 ? Object.values(listFolders)[0] : 0;
        if(firstValue.size > 0){
          // 2 - Cargamos subcarpetas
          this.showFiles      = false;
          this.showDetail     = false;
          this.showNoElements = false;
          this.documentationListSubFolders = listFolders;
        } else {
          // 3 - No tiene sub carpetas, cargamos los documentos
          const filterDocument = new FilterBuilder();
          filterDocument.addField('Path', folder);
          this.documentService.getDocumentList(filterDocument.getFilter()).then(documentsList => {
            // 4 - Se mira si tenemos documentos o no, Si no tenemos:
            // this.showNoElements = true, para mostrar el mensaje correspondiente
            const firstValue    = Object.keys(documentsList).length > 0 ? Object.values(documentsList)[0] : 0;
            this.showNoElements = firstValue.size > 0 ? false : true;
            // Agregamos los datos devueltos
            this.documentsList = documentsList;
          })
          this.showFiles  = true;
        }
       
        if(!this.showMenu){
          // Mostramos el menu
          this.showMenu = true;
          // Marcamos la carpeta seleccionada
          this.selecFolder = id;
        }
      }
    })
  }

  /*
    Devuelve el nombre con la extension (archivo.png)
  */
  getNameWithExtension(name: string, extension: string): string {
    const finalExtension = extension.split('/').pop();
    return name+'.'+finalExtension;
  }
  
  /*
    La carpeta que se esta visualizando
  */
  selectFolder(id: string){
    console.log(this.selecFolder)
    console.log('select folder')
    // Eliminamos si existe otro marcado
    if(this.selecFolder === ''){
      this.selecFolder = ''
    }
    const elements = document.getElementsByClassName('select-folder');
    while(elements.length > 0){
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
  closeShowDocumentation(){
    // Eliminamos clase si existe un elemento ya marcado
    this.deleteViewFile();
    // Quitamos el visualizador
    this.showDetail = false;
  }

  /*
    Mover un documento a otra carpeta
    (seria uno varios)
  */
  getMoveFile(folder: string){
    console.log(folder);
  }

  /*
    El documento que se esta visualizando
  */
    deleteViewFile(){
      // Eliminamos clase si existe un elemento ya marcado
      const elements = document.getElementsByClassName('select-view');
      while(elements.length > 0){
        elements[0].classList.remove('select-view');
      }
    }
    selectViewFile(id: string){
      // Cogemos el div principal del card
      let element = document.getElementById(id);
      // Si no existe la agregamos, si existe la removemos
      if(!element!.classList.contains('select')){
        element!.classList.add('select');
      } else {
        element!.classList.remove('select');
      }
    }

}
