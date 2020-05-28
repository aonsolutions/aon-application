import { AngularFireModule } from '@angular/fire';
import { AngularFireStorageModule } from '@angular/fire/storage';
import { environment } from '../environments/environment';
import { BrowserModule } from '@angular/platform-browser';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { DeviceDetectorModule } from 'ngx-device-detector';
import { AppComponent } from './app.component';
import { AonLoginComponent } from './components/aon-login/aon-login.component';
import { UserRegisterComponent } from './components/user-register/user-register.component';
import { UserEditComponent } from './components/user-edit/user-edit.component';
import { UserChangePasswordComponent } from './components/user-change-password/user-change-password.component';
import { AonToolbarComponent } from './components/aon-toolbar/aon-toolbar.component';
import { UserDialogComponent } from './components/dialogs/user-dialog/user-dialog.component';
import { UserTableComponent } from './components/user-table/user-table.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from './material/material.module';
import { InvoiceModule } from './invoice/invoice.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AonCompanyListComponent } from './components/aon-company-list/aon-company-list.component';
import { CompanyService, SharedService, UserService, ExcelService, AonService } from './services/services';
import { MyAccountComponent } from './components/my-account/my-account.component';
import { PrinterConfigurationComponent } from './components/printer-configuration/printer-configuration.component';
import { UserListDialogComponent } from './components/dialogs/user-list-dialog/user-list-dialog.component';
import { CompanyListDialogComponent } from './components/dialogs/company-list-dialog/company-list-dialog.component';
import { ErrorDialogComponent } from './components/dialogs/error-dialog/error-dialog.component';
import { SureDialogComponent } from './components/dialogs/sure-dialog/sure-dialog.component';
import { RememberPasswordDialogComponent } from './components/dialogs/remember-password-dialog/remember-password-dialog.component';
import { SendDialogComponent } from './components/dialogs/send-dialog/send-dialog.component';
import { CommentDialogComponent } from './components/dialogs/comment-dialog/comment-dialog.component';
import { AddressDialogComponent } from './components/dialogs/address-dialog/address-dialog.component';
import { TypeDialogComponent } from './components/dialogs/type-dialog/type-dialog.component';
import { ExtensionDialogComponent } from './components/dialogs/extension-dialog/extension-dialog.component';
import { AdvancedSearchDialogComponent } from './components/dialogs/advanced-search-dialog/advanced-search-dialog.component';
import { AdvancedUserSearchDialogComponent } from './components/dialogs/advanced-user-search-dialog/advanced-user-search-dialog.component';
import { AdvancedCompanySearchDialogComponent } from './components/dialogs/advanced-company-search-dialog/advanced-company-search-dialog.component';
import { AonMenuSidenavComponent } from './components/aon-menu-sidenav/aon-menu-sidenav.component';


import { TediSearchBoxComponent } from './components/tedi-search-box/tedi-search-box.component';
import { TediMobileSearchBoxComponent } from './components/tedi-mobile-search-box/tedi-mobile-search-box.component';
import { ScrollDispatchModule } from '@angular/cdk/scrolling';
import { TediNoDropZoneDirective } from './directives/no-drop-zone.directive';
import { TediInitValueDirective } from './directives/tedi-init-value.directive';

import {NgxImageCompressService } from 'ngx-image-compress';

@NgModule({
  declarations: [
    AppComponent,
    AonLoginComponent,
    AonMenuSidenavComponent,
    UserRegisterComponent,
    UserEditComponent,
    UserChangePasswordComponent,
    AonToolbarComponent,
    UserDialogComponent,
    UserTableComponent,
    AonCompanyListComponent,
    MyAccountComponent,
    PrinterConfigurationComponent,
    TediSearchBoxComponent,
    TediMobileSearchBoxComponent,
    UserListDialogComponent,
    CompanyListDialogComponent,
    RememberPasswordDialogComponent,
    ErrorDialogComponent,
    SureDialogComponent,
    SendDialogComponent,
    CommentDialogComponent,
    AddressDialogComponent,
    TypeDialogComponent,
    ExtensionDialogComponent,
    AdvancedSearchDialogComponent,
    AdvancedUserSearchDialogComponent,
    AdvancedCompanySearchDialogComponent,
    TediNoDropZoneDirective,
    TediInitValueDirective
  ],
  imports: [
    InfiniteScrollModule,
    ScrollDispatchModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MaterialModule,
    ReactiveFormsModule,
    AppRoutingModule,
    FormsModule, // Origami requires the Angular Forms module
    InvoiceModule,
    AngularFireModule.initializeApp(environment.firebase),
    AngularFireStorageModule,
    DeviceDetectorModule.forRoot()
  ],
  entryComponents: [
    UserDialogComponent,
    UserListDialogComponent,
    CompanyListDialogComponent,
    ErrorDialogComponent,
    SendDialogComponent,
    CommentDialogComponent,
    AddressDialogComponent,
    TypeDialogComponent,
    ExtensionDialogComponent,
    SureDialogComponent,
    RememberPasswordDialogComponent,
    AdvancedSearchDialogComponent,
    AdvancedUserSearchDialogComponent,
    AdvancedCompanySearchDialogComponent
  ],
  providers: [SharedService, UserService, CompanyService, NgxImageCompressService, ExcelService, AonService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent]
})
export class AppModule { }
