import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[tediInvoiceInitValue]'
})

export class TediInvoiceInitValueDirective {

  constructor(private el: ElementRef) {
    (el.nativeElement as HTMLInputElement).value = '';
  }

  ngOnInit() {
    if((this.el.nativeElement as HTMLInputElement).value === 'undefined') {
      (this.el.nativeElement as HTMLInputElement).value = '';
    }
  }
}
