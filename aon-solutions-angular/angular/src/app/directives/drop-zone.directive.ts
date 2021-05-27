import { Directive, HostListener, HostBinding, EventEmitter, Output} from '@angular/core';

@Directive({
  selector: '[tediDropZone]'
})

export class TediDropZoneDirective {
  @Output() private filesChangeEmiter: EventEmitter<File[]> = new EventEmitter();
  @HostBinding('style.border') private border = '';
  @HostBinding('style.background') private background = '';

  borderOff = '';
  borderOn = '';
  backgroundOff = '';
  backgroundOn = 'lightgray';

  constructor() {
  }

  @HostListener('dragover', ['$event']) onDragOver(event) {
    event.preventDefault();
    return false;
  }

  @HostListener('dragenter', ['$event']) onDragEnter(event) {
    this.border = this.borderOn;
    this.background = this.backgroundOn;
    event.preventDefault();
  }
  @HostListener('dragleave', ['$event']) onDragLeave(event) {
    this.border = this.borderOff;
    this.background = this.backgroundOff;
    event.preventDefault();
  }

  @HostListener('drop', ['$event']) public onDrop(evt) {
    this.border = this.borderOff;
    this.background = this.backgroundOff;
    evt.preventDefault();
    evt.stopPropagation();
    const files = evt.dataTransfer.files;
    if (files.length > 0) {
      this.filesChangeEmiter.emit(files);
    }
  }
}
