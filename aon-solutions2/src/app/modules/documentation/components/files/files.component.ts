import { Component, OnInit } from '@angular/core';

import { Document } from 'src/app/core/models/class/document';
import { DocumentService } from 'src/app/core/services/document.service';
import { Folder } from 'src/app/core/models/class/folder';
import { FolderService } from 'src/app/core/services/folder.service';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.scss']
})
export class FilesComponent implements OnInit {

  detail: boolean = false;

  documentationListFolders: Folder[] = [];
  documentsList: Document[] = [];

  constructor(folderService: FolderService, documentService: DocumentService) {

    documentService.getDocumentList().then(documentsList => {
      this.documentsList = documentsList;
      console.log(documentsList);
    });

    folderService.getFolderList().then(listFolders => {
      this.documentationListFolders = listFolders;
    });
  }

  ngOnInit(): void {
  }

  getNameWithExtension(name: string, extension: string): string {
    const finalExtension = extension.split('/').pop();

    return name+'.'+finalExtension;
  }

  getFormattedDate(): string {
    const currentDate = new Date();
    const day = String(currentDate.getDate()).padStart(2, '0');
    const month = String(currentDate.getMonth() + 1).padStart(2, '0');
    const year = String(currentDate.getFullYear()).slice(-2);
    const formattedDate = `${day}/${month}/${year}`;

    return formattedDate;
  }

}
