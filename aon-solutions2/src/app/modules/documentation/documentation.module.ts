import { NgModule }           from '@angular/core';
import { CommonModule }       from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule }  from '@angular/material/grid-list';
import { MatMenuModule }      from '@angular/material/menu';

import { DocumentationRoutingModule } from './documentation-routing.module';
import { DocumentationComponent }     from './pages/documentation.component';
//import { DocumentRouterComponent } from './components/document-router/document-router.component';
//import { FilesComponent } from './components/files/files.component';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [
    DocumentationComponent,
//    DocumentRouterComponent,
//    FilesComponent,
  ],
  imports: [
    CommonModule,
    DocumentationRoutingModule,
    MatFormFieldModule,
    MatGridListModule,
    MatMenuModule,
    SetMaterialModule,
    SharedModule
  ]
})
export class DocumentationModule { }
