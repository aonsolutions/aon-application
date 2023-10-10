import { NgModule }           from '@angular/core';
import { CommonModule }       from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule }  from '@angular/material/grid-list';
import { MatMenuModule }      from '@angular/material/menu';

import { DocumentationRoutingModule } from './documentation-routing.module';
import { DocumentationComponent }     from './pages/documentation.component';
  import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';
import { RenameFileComponent } from './components/rename-file/rename-file.component';

@NgModule({
  declarations: [
    DocumentationComponent,
    RenameFileComponent,
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
