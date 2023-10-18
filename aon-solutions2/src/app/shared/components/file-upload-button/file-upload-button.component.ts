import {
  Component,
  EventEmitter,
  HostBinding,
  OnInit,
  Output,
} from '@angular/core';

@Component({
  selector: 'app-file-upload-button',
  templateUrl: './file-upload-button.component.html',
  styleUrls: ['./file-upload-button.component.scss'],
})
export class FileUploadButtonComponent implements OnInit {
  width: string = '100%';
  height: string = '100%';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';
  @Output() getUploadedFiles: EventEmitter<FileList> = new EventEmitter();

  constructor() {
    this.widthHost = this.width;
    this.heightHost = this.height;
  }

  isDropOver: boolean = false;

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
      const files = event.dataTransfer.files;
      this.emitDrop(files);
    }

    this.isDropOver = false;
  }

  emitDrop(fileList: FileList) {
    this.getUploadedFiles.emit(fileList);
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
