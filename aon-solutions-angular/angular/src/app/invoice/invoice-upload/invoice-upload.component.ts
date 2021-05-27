import { Component, OnInit, ViewChild, ElementRef, Input } from '@angular/core';
import { SharedService } from '../../services/shared.service';


@Component({
  selector: 'app-invoice-upload',
  templateUrl: './invoice-upload.component.html',
  styleUrls: ['./invoice-upload.component.css']
})
export class InvoiceUploadComponent implements OnInit {

  @Input() fileList: Array<File>;

  @ViewChild('fileInput', {static: false}) fileInput: ElementRef;
  @ViewChild('container', {static: false}) container: ElementRef;

  constructor(private sharedService: SharedService) {

  }

  ngOnInit() { }

  onSelectFiles() {
    this.fileInput.nativeElement.click();
  }
  onDialogFileSelected() {
    this.fire(this.fileInput.nativeElement.files);
  }
  onFilesChange(fileList: Array<File>) {
    this.fire(fileList);
  }
  fire(fileList: Array<File>) {

    this.container.nativeElement.style.display = (fileList && fileList.length > 0) ? 'block' : 'none';
    this.sharedService.filesToUpload.next(fileList);
  }
}
