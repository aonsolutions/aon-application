import { Component, OnInit } from '@angular/core';

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


  constructor(folderService: FolderService) {
    folderService.getFolderList().then(listFolders => {
      this.documentationListFolders = listFolders;
    });
  }

  ngOnInit(): void {
  }

}
