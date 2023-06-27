import { DocumentRouterComponent } from './components/document-router/document-router.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FilesComponent } from './components/files/files.component';
import { DocumentationComponent } from './components/documentation/documentation.component';

const routes: Routes = [
  {
    path: '',
    component: DocumentRouterComponent,
    children: [
      {
        path: '',
        component: DocumentationComponent
      },
      {
        path: 'contabilizado',
        component: FilesComponent,
      }
    ],
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DocumentationRoutingModule { }
