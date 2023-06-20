import { LoginComponent } from './components/login/login.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SelectEnterpriseComponent } from './components/select-enterprise/select-enterprise.component';
import { LoginGuard } from 'src/app/core/guards/login.guard';
import { EnterpriseGuard } from 'src/app/core/guards/enterprise.guard';

const routes: Routes = [
  {
    path: '',
    component: LoginComponent,
    canActivate: [LoginGuard],
  },
  {
    path: 'selectEnterprise',
    component: SelectEnterpriseComponent,
    canActivate: [EnterpriseGuard],
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
