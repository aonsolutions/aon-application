import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
  selector: '[appGlobalHover]'
})
export class GlobalHoverDirective {

  @Input() backgroundColor : string = '';
  @Input() borderColor: string = '';
  @Input() oldBackgroundColor : string = '';
  @Input() oldBorderColor: string = '';
  @Input() oldColor : string = '';
  @Input() color: string = '';

  constructor(private el: ElementRef) { }

  @HostListener('mouseenter') onMouseEnter() {
    this.highlight(this.backgroundColor, this.borderColor, this.color);
  }
  
  @HostListener('mouseleave') onMouseLeave() {
    this.highlight(this.oldBackgroundColor,this.oldBorderColor, this.oldColor);
  }
  
  private highlight(backgroundColor: string, borderColor: string, color: string) {
    if(this.oldBackgroundColor != '' && this.backgroundColor != '')
      this.el.nativeElement.style.backgroundColor = backgroundColor;
    if(this.oldBorderColor != '' && this.borderColor != '')
      this.el.nativeElement.style.borderColor = borderColor;
    if(this.oldColor != '' && this.color != '')
      this.el.nativeElement.style.color = color;
  }

}
