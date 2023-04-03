import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  // { path: '**', redirectTo: 'main', pathMatch: 'full' },
  { path: '', redirectTo: 'aon', pathMatch: 'full' },
  {
    path: 'aon',
    loadChildren: () =>
      import('src/app/core/core.module').then((m) => m.CoreModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
