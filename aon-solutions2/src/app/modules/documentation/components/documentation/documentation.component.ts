import { Folder } from './../../../../core/models/class/folder';
import { Component, OnInit } from '@angular/core';
import { FolderService } from 'src/app/core/services/folder.service';

@Component({
  selector: 'app-documentation',
  templateUrl: './documentation.component.html',
  styleUrls: ['./documentation.component.scss']
})
export class DocumentationComponent implements OnInit {

  documentationListFolders: Folder[] = [];

  constructor(folderService: FolderService) {
    folderService.getFolderList().then(listFolders => {
      this.documentationListFolders = listFolders;
    });
  }

  ngOnInit(): void {
  }

}
