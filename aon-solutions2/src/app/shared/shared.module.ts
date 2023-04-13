import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';


@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    MatMenuModule,
    SetMaterialModule
  ],
  exports:[
  ]
})
export class SharedModule { }
