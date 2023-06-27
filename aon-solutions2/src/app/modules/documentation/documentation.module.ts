import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { NgModule } from '@angular/core';

import { DocumentationComponent } from './components/documentation/documentation.component';
import { DocumentationRoutingModule } from './documentation-routing.module';
import { DocumentRouterComponent } from './components/document-router/document-router.component';
import { FilesComponent } from './components/files/files.component';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    DocumentationComponent,
    DocumentRouterComponent,
    FilesComponent
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
