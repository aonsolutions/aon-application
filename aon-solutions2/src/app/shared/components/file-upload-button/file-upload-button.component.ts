import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-file-upload-button',
  templateUrl: './file-upload-button.component.html',
  styleUrls: ['./file-upload-button.component.scss']
})
export class FileUploadButtonComponent implements OnInit {

  @Input() width : string = 'auto';
  @Input() height : string = 'auto';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';

  @Output()  getUploadedFiles : EventEmitter<FileList> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
    this.widthHost = this.width;
    this.heightHost = this.height;
  }

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.getUploadedFiles.emit(target.files);
    }
  }

  onDrop(event : any){
    this.getUploadedFiles.emit(event);
  }

}
