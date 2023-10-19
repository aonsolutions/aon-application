import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UploadModalComponent } from './components/upload-modal/upload-modal.component';

@Component({
  selector: 'app-file-upload-button',
  templateUrl: './file-upload-button.component.html',
  styleUrls: ['./file-upload-button.component.scss'],
})
export class FileUploadButtonComponent implements OnInit {
  width: string = '100%';
  height: string = '100%';
  isDropOver: boolean = false;
  @Input() isOpenUploadModal: boolean = false;
  @ViewChild('modal') modalComponent: any = '';
  @HostBinding('style.--widthHost') widthHost = '';
  @HostBinding('style.--heightHost') heightHost = '';
  @Output() getUploadedFiles: EventEmitter<FileList> = new EventEmitter();

  constructor() {
    this.widthHost = this.width;
    this.heightHost = this.height;
  }

  ngOnInit(): void {}

  // Modal para subir el archivo
  uploadDocument(event: Event) {
    if(!this.isOpenUploadModal){
      event.preventDefault();
      this.modalComponent.openDialog(UploadModalComponent);
    }
  }

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
