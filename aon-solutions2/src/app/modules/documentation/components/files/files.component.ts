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
    });

    folderService.getFolderList().then(listFolders => {
      this.documentationListFolders = listFolders;
    });
  }

  ngOnInit(): void {
  }

}
