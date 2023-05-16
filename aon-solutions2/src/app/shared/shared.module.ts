import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';
import { SidenavHoverDirective } from './directives/sidenav-hover.directive';
import { ExampleLayoutComponent } from './layouts/example-layout/example-layout.component';


@NgModule({
  declarations: [
    SidenavHoverDirective,
    ExampleLayoutComponent
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
