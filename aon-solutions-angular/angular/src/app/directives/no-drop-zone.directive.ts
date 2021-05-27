import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[tediNoDropZone]'
})

export class TediNoDropZoneDirective {

  constructor() {
  }

  @HostListener('dragover', ['$event']) onDragOver(event) {
    event.preventDefault();
  }

  @HostListener('dragenter', ['$event']) onDragEnter(event) {
    event.dataTransfer.effectAllowed = 'none';
    event.dataTransfer.dropEffect = 'none';
    event.preventDefault();
  }
  @HostListener('dragleave', ['$event']) onDragLeave(event) {
    event.dataTransfer.effectAllowed = 'all';
    event.dataTransfer.dropEffect = 'copy';
    event.preventDefault();
  }
  @HostListener('drop', ['$event']) public onDrop(event) {
    event.preventDefault();
    event.stopPropagation();
  }

}
