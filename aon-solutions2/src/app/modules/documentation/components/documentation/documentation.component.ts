import { Component, OnInit } from '@angular/core';
import { DocumentService } from 'src/app/core/services/document.service';
import { FolderService } from 'src/app/core/services/folder.service';
import { CollectionFactory, Factory, FilterBuilder, ICollection, IDocument, IFolder } from 'libraries/AonSDK/aon';

@Component({
  selector: 'app-documentation',
  templateUrl: './documentation.component.html',
  styleUrls: ['./documentation.component.scss']
})
export class DocumentationComponent implements OnInit {

  collectionFactory = new CollectionFactory();
  factory = new Factory();
  documentationFilteredFolders: ICollection<IFolder> = this.collectionFactory.createFolderCollection();
  documentationListFolders: ICollection<IFolder> = this.collectionFactory.createFolderCollection();
  documentsList: ICollection<IDocument> = this.collectionFactory.createDocumentCollection();
  fileToShow: IDocument = this.factory.createDocument();
  showDetail: boolean = false;
  showFiles: boolean = false;
  showMenu: boolean = false;
  isListFiltered: boolean = false;


  constructor(private documentService: DocumentService, private folderService: FolderService) {
    /*
      Inicialmente solo devolvemos carpetas correspondientes a la raíz
      es por ello que no pasamos parámetro, para que por defecto sea ''
    */
    this.getFolders();
  }

  ngOnInit(): void {
  }

  getFolders(parentFolder: string = '') {
    let filter = new FilterBuilder();
    filter.addField('parent', parentFolder);
    this.folderService.getFolderList(filter.getFilter()).then(listFolders => {
      if (parentFolder === '') {
        this.documentationListFolders = listFolders;
      } else {
        this.documentationFilteredFolders = listFolders;
        this.isListFiltered = true;
      }
    })
  }

  getFiles(folder: IFolder) {
    this.showFiles = true;
    let filter = new FilterBuilder();
    filter.addField('Path', folder.Path);
    this.documentService.getDocumentList(filter.getFilter()).then(documentsList => {
      this.documentsList = documentsList;
    })
  }

  loadFile(file: IDocument) {
    // let ext = file.FileType.split('/')[0];
    console.log(file);
    this.fileToShow = file;

    return file;
  }

  getNameWithExtension(name: string, extension: string): string {
    const finalExtension = extension.split('/').pop();

    return name+'.'+finalExtension;
  }

}
