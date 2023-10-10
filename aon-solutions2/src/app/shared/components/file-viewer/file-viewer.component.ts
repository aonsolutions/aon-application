import { Component, Input, OnInit } from '@angular/core';
import { IDocument } from 'libraries/AonSDK/src/aon';
import { DocumentService } from 'src/app/core/services/document.service';
import { FileToBase64 } from 'src/app/core/utilities/file';

@Component({
  selector: 'app-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnInit {
  @Input() pdfSrc : IDocument = this.documentService.objectFactory.createDocument();
  @Input() zoom   : string = 'page-width'
  @Input() locale : string = 'es-ES';
  pdfBytes        : any;

  constructor(private documentService: DocumentService){

  }
    
  ngOnInit(): void {
    this.documentService.getDocumentFile(this.pdfSrc).then((response) => {
      if(this.pdfSrc.FileType.split('/')[1] === 'pdf')
        response.arrayBuffer().then((response) => {
          const arr1 = new Uint8Array(response);
          this.pdfBytes = arr1
        })
      else 
        FileToBase64(new File([response], this.pdfSrc.FileName)).then((response) => {
          this.pdfBytes = response;
        })
      })
  }

}
