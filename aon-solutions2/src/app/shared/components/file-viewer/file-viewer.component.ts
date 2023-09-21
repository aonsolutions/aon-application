import { Component, Input, OnInit } from '@angular/core';
import { Base64ToArrayBuffer } from 'src/app/core/utilities/file';

@Component({
  selector: 'app-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnInit {
  @Input() pdfSrc : string = '';
  @Input() zoom   : string = 'page-width'
  @Input() locale : string = 'es-ES';
  pdfBytes        : any;

  constructor(
  ) { }

  ngOnInit(): void {
    this.pdfBytes = Base64ToArrayBuffer(this.pdfSrc);
  } 

}
