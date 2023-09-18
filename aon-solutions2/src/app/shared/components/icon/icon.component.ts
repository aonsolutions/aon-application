import { Component, Input, OnInit } from '@angular/core';
import * as myGlobals from '../../../app.globals';

@Component({
  selector    : 'app-icon',
  templateUrl : './icon.component.html',  
  styleUrls   : ['./icon.component.scss']
})

export class IconComponent implements OnInit {
  @Input() shape          : string  = '';
  @Input() size           : string  = '1.2rem';
  @Input() color          : string  = 'black';
  @Input() backgroundColor: string  = '';
  @Input() border         : string  = '';
  @Input() borderRadius   : string  = '';
  @Input() padding        : string  = '';
  matIcon                 : boolean = false;

  constructor() {
  }
  
  ngOnInit(): void {
    // Si esta agregado al proyecto se usa mat-icon
    if(myGlobals.aon_add_icon[this.shape] !== undefined)
      this.matIcon = true;
  }

}
