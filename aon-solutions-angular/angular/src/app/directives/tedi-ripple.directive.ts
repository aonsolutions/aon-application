import { Directive, HostListener, HostBinding} from '@angular/core';
import { MatRipple } from '@angular/material';

@Directive({
  selector: '[tedi-ripple]',
  providers: [ MatRipple ]
})
export class TediRippleDirective {

  constructor(
    private ripple: MatRipple
  ) { }

  @HostBinding('style.background-color') color: string;
  @HostBinding('style.position') position: string;

  @HostListener('click', [ '$event' ]) clickEvent(event) {
    // STYLES
    this.position = 'relative';
    //  this.color = '#ddd';

    this.ripple.unbounded = false;
    this.ripple.launch(event.x, event.y);
  }
}
