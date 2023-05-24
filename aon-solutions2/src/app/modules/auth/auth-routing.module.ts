import { LoginComponent } from './components/login/login.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SelectEnterpriseComponent } from './components/select-enterprise/select-enterprise.component';

const routes: Routes = [
  {
    path: '',
    component: LoginComponent
  },
  {
    path: 'selectEnterprise',
    component: SelectEnterpriseComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
