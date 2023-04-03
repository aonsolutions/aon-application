import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BandejaRoutingModule } from './bandeja-routing.module';
import { BandejaComponent } from './bandeja.component';

@NgModule({
  declarations: [
    BandejaComponent
  ],
  imports: [
    CommonModule,
    BandejaRoutingModule
  ]
})
export class BandejaModule { }
