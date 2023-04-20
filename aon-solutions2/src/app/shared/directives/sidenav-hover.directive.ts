import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
  selector: '[appSidenavHover]'
})
export class SidenavHoverDirective{

  @Input() selected : string = 'false';
  @Input() color : string = '';

  constructor(private el: ElementRef) { }

  @HostListener('mouseenter') onMouseEnter() {
    if(this.selected == 'false')
      this.highlight(this.color);
  }
  
  @HostListener('mouseleave') onMouseLeave() {
    if(this.selected == 'false')
      this.highlight('');
  }
  
  private highlight(color: string) {
    this.el.nativeElement.style.backgroundColor = color;
  }

}
