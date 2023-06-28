import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnInit {

  @Input() pdfSrc: string = "https://vadimdez.github.io/ng2-pdf-viewer/assets/pdf-test.pdf";
  @Input() zoom: string = 'page-width'
  @Input() locale: string = 'es-ES';

  constructor() { }

  ngOnInit(): void {
  }


}
