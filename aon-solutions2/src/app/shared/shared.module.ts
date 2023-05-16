import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';
import { SidenavHoverDirective } from './directives/sidenav-hover.directive';


@NgModule({
  declarations: [
    SidenavHoverDirective
  ],
  imports: [
    CommonModule,
    MatMenuModule,
    SetMaterialModule
  ],
  exports:[
    SidenavHoverDirective
  ]
})
export class SharedModule { }
