import { Component, Input, OnInit } from '@angular/core';

import { Document } from 'src/app/core/models/class/document';
import { DocumentService } from 'src/app/core/services/document.service';
import { Folder } from 'src/app/core/models/class/folder';
import { FolderService } from 'src/app/core/services/folder.service';
import { MenuButton } from 'src/app/core/models/interface/menu-button';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.scss']
})
export class FilesComponent implements OnInit {

  detail: boolean = false;
  showFiles: boolean = false;

  documentationFolders: Folder[] = [];
  documentationFilteredFolders: Folder[] = [];
  documentsList: Document[] = [];

  fileOptions: MenuButton[] = [
    {routerlink: '#', shape: 'eye', text: 'Vista previa', class: '', selected: false},
    {routerlink: '#', shape: 'edit', text: 'Cambiar nombre', class: '', selected: false},
    {routerlink: '#', shape: 'bookmark', text: 'Etiquetar como', class: '', selected: false},
    {routerlink: '#', shape: 'cloud_download', text: 'Descargar', class: '', selected: false},
    {routerlink: '#', shape: 'delete', text: 'Eliminar', class: '', selected: false}
  ];


  constructor(private folderService: FolderService, private documentService: DocumentService, private route: ActivatedRoute) {
    const tipo = this.route.snapshot.data.tipo;

    this.folderService.getFolderList().then(listFolders => {
      // TODO: Mejorar para que esta lista venga desde documentation
      this.documentationFolders = listFolders.filter(folder => folder.Parent === '/')
      // Filtrar listFolders por tipo
      this.documentationFilteredFolders = listFolders.filter(folder => folder.Parent.startsWith(`/${tipo}`));
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

  getFiles(folder: Folder) {
    this.showFiles = true;

    this.documentService.getDocumentList().then(documentsList => {
      debugger;
      console.log(documentsList);
      this.documentsList = documentsList.filter(document => document.Folder === folder.Path);
    })


    console.log('Actualizamos lista ficheros');
  }

}
