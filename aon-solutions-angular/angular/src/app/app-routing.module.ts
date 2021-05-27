import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AonLoginComponent } from './components/aon-login/aon-login.component';
import { AonCompanyListComponent } from './components/aon-company-list/aon-company-list.component';
import { MyAccountComponent } from './components/my-account/my-account.component';
import { PrinterConfigurationComponent } from './components/printer-configuration/printer-configuration.component';
import { UserTableComponent } from './components/user-table/user-table.component';
import { UserRegisterComponent } from './components/user-register/user-register.component';
import { UserEditComponent } from './components/user-edit/user-edit.component';
import { UserChangePasswordComponent } from './components/user-change-password/user-change-password.component';

// COMPANY COMPONENTS


// INVOICE components

import { InvoiceComponent } from './invoice/invoice/invoice.component';
import { InvoiceTableComponent } from './invoice/invoice-table/invoice-table.component';
import { InvoiceMobileListComponent } from './invoice/invoice-mobile-list/invoice-mobile-list.component';
import { InvoiceSheetComponent } from './invoice/invoice-sheet/invoice-sheet.component';
import { InvoiceUploadComponent } from './invoice/invoice-upload/invoice-upload.component';

const routes: Routes = [
  {path: 'login', component: AonLoginComponent},
  {path: 'invoice', component: InvoiceComponent, children: [
    {path: 'table', component: InvoiceTableComponent},
    {path: 'list', component: InvoiceMobileListComponent},
    {path: 'sheet', component: InvoiceSheetComponent},
    {path: 'upload', component: InvoiceUploadComponent}
  ]},
  {path: 'myAccount', component: MyAccountComponent, children: [
    {path: '', component: UserEditComponent},
    {path: 'general', component:  UserEditComponent},
    {path: 'users', component:  UserTableComponent},
    {path: 'createUser', component: UserRegisterComponent},
    {path: 'editUser', component: UserEditComponent},
    {path: 'changePassword', component: UserChangePasswordComponent},
    {path: 'printer', component:  PrinterConfigurationComponent}
  ]},
  {path: 'companyList', component: AonCompanyListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
