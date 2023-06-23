import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { NgModule } from '@angular/core';

import { DocumentationComponent } from './components/documentation/documentation.component';
import { DocumentationRoutingModule } from './documentation-routing.module';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    DocumentationComponent
  ],
  imports: [
    CommonModule,
    DocumentationRoutingModule,
    MatFormFieldModule,
    MatGridListModule,
    SetMaterialModule,
    SharedModule
  ]
})
export class DocumentationModule { }
