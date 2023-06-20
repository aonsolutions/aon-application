import { AuthGuard } from './guards/auth.guard';
import { BasicLayoutComponent } from '../shared/layouts/basic-layout/basic-layout.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SidenavLayoutComponent } from '../shared/layouts/sidenav-layout/sidenav-layout.component';

const routes: Routes = [
  {
    path: '',
    component: SidenavLayoutComponent,
    children: [
      {
        path: 'home',
        loadChildren: () =>
          import('src/app/modules/home/home.module').then((m) => m.HomeModule),
      },
      {
        path: 'inbox',
        loadChildren: () =>
          import('src/app/modules/inbox/inbox.module').then((m) => m.InboxModule),
      },
      // {
      //   path: 'billing',
      //   loadChildren: () =>
      //     import('src/app/modules/billing/billing.module').then((m) => m.BillingModule),
      // },
      // {
      //   path: 'consulting',
      //   loadChildren: () =>
      //     import('src/app/modules/consulting/consulting.module').then((m) => m.ConsultingModule),
      // },
      // {
      //   path: 'documentation',
      //   loadChildren: () =>
      //     import('src/app/modules/documentation/documentation.module').then((m) => m.DocumentationModule),
      // },
      // {
      //   path: 'employee-panel',
      //   loadChildren: () =>
      //     import('src/app/modules/employee-panel/employee-panel.module').then((m) => m.EmployeePanelModule),
      // },
      // {
      //   path: 'tax-panel',
      //   loadChildren: () =>
      //     import('src/app/modules/tax-panel/tax-panel.module').then((m) => m.TaxPanelModule),
      // },
      {
        path: '', redirectTo: 'home', pathMatch: 'full'
      },
    ], canActivate: [AuthGuard],
  },
  {
    path: 'auth',
    component: BasicLayoutComponent,
    children:[
      {
        path: '',
        loadChildren: () =>
          import('src/app/modules/auth/auth.module').then((m) => m.AuthModule),
      }
    ]
  },
  { path: '**', redirectTo: 'home', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CoreRoutingModule {}
