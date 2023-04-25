import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';
import { SidenavHoverDirective } from './directives/sidenav-hover.directive';
import { CustomSelectComponent } from './components/custom-select/custom-select.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    SidenavHoverDirective,
    CustomSelectComponent
  ],
  imports: [
    CommonModule,
    MatMenuModule,
    SetMaterialModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule
  ],
  exports:[
    SidenavHoverDirective,
    CustomSelectComponent
  ]
})
export class SharedModule { }
