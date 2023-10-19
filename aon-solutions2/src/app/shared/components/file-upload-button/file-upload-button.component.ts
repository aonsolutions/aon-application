import { Component, EventEmitter, HostBinding, OnInit, Output } from '@angular/core';

@Component({
  selector    : 'app-file-upload-button',
  templateUrl : './file-upload-button.component.html',
  styleUrls   : ['./file-upload-button.component.scss'],
})
export class FileUploadButtonComponent implements OnInit {
  width     : string  = '100%';
  height    : string  = '100%';
  isDropOver: boolean = false;
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';
  @Output() getUploadedFiles: EventEmitter<FileList> = new EventEmitter();

  constructor() {
    this.widthHost  = this.width;
    this.heightHost = this.height;
  }

  ngOnInit(): void {}

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.getUploadedFiles.emit(target.files);
    }
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer && event.dataTransfer.files) {
      this.getUploadedFiles.emit(event.dataTransfer.files);
    }

    this.isDropOver = false;
  }

  onDragOver(event: any) {
    event.preventDefault();
    this.isDropOver = true;
  }

  onDragLeave(event: any) {
    event.preventDefault();
    this.isDropOver = false;
  }
}
