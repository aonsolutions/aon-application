import { Directive, EventEmitter, HostBinding, HostListener, Input, Output } from '@angular/core';

@Directive({
  selector: '[appDragAndDrop]'
})
export class DragAndDropDirective {

  constructor() { }
  @Output() private filesChangeEmiter : EventEmitter<File[]> = new EventEmitter();

  @HostListener('drop', ['$event'])
  public onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    if(event.dataTransfer?.files != null){
      let files = event.dataTransfer.files;
      let valid_files : any = files;
      this.filesChangeEmiter.emit(valid_files);
    }
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event: DragEvent) {
    event.stopPropagation();
    event.preventDefault();
  }

  @HostListener('dragleave', ['$event'])
  onDragLeave(event: DragEvent) {
    event.stopPropagation();
    event.preventDefault();
  }

}
