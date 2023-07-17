import { Component, Input, OnInit } from '@angular/core';

import { Document } from 'src/app/core/models/class/document';
import { DocumentService } from 'src/app/core/services/document.service';
import { Folder } from './../../../../core/models/class/folder';
import { FolderService } from 'src/app/core/services/folder.service';

@Component({
  selector: 'app-documentation',
  templateUrl: './documentation.component.html',
  styleUrls: ['./documentation.component.scss']
})
export class DocumentationComponent implements OnInit {

  documentationFilteredFolders: Folder[] = [];
  documentationListFolders: Folder[] = [];
  documentsList: Document[] = [];
  fileToShow: Document = new Document();
  showDetail: boolean = false;
  showFiles: boolean = false;
  showMenu: boolean = false;
  isListFiltered: boolean = false;


  constructor(private documentService: DocumentService, private folderService: FolderService) {
    /*
      Inicialmente solo devolvemos carpetas correspondientes a la raíz
      es por ello que no pasamos parámetro, para que por defecto sea '/'
    */
    this.getFolders();
  }

  ngOnInit(): void {
  }

  getFolders(parentFolder: string = '/') {
    this.folderService.getFolderList().then(listFolders => {
      if (parentFolder === '/') {
        this.documentationListFolders = listFolders.filter(folder => folder.Parent === parentFolder);
      } else {
        this.documentationFilteredFolders = listFolders.filter(folder => folder.Parent === parentFolder);
        this.isListFiltered = true;
      }
    })
  }

  getFiles(folder: Folder) {
    this.showFiles = true;

    this.documentService.getDocumentList().then(documentsList => {
      console.log(documentsList);
      this.documentsList = documentsList.filter(document => document.Folder === folder.Path);
    })
  }

  loadFile(file: Document) {
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
