import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[tediInitValue]'
})

export class TediInitValueDirective {

  constructor(private el: ElementRef) {
    (el.nativeElement as HTMLInputElement).value = '';
  }

  ngOnInit() {
    if((this.el.nativeElement as HTMLInputElement).value === 'undefined') {
      (this.el.nativeElement as HTMLInputElement).value = '';
    }
  }
}
