import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
//import { DocumentRouterComponent } from './components/document-router/document-router.component';
//import { FilesComponent } from './components/files/files.component';
import { DocumentationComponent } from './pages/documentation.component';

const routes: Routes = [
//  {
//    path: '',
//    component: DocumentRouterComponent,
//    children: [
//      {
//        path: '',
//        component: DocumentationComponent
//      },
//      {
//        path: 'contabilizado',
//        component: FilesComponent,
//      }
//    ],
//  }
  {
    path: '',
    component: DocumentationComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DocumentationRoutingModule { }
