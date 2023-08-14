import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileCompanyComponent } from './page/profile-company.component';
import { ProfileCompanyRoutingModule } from './profile-company-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { TabsProfileCompanyComponent } from './components/tabs-profile-company/tabs-profile-company.component';



@NgModule({
  declarations: [
    ProfileCompanyComponent,
    TabsProfileCompanyComponent
  ],
  imports: [
    CommonModule,
    ProfileCompanyRoutingModule,
    SharedModule

  ]
})
export class ProfileCompanyModule { }
