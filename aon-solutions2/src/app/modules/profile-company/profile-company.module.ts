import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileCompanyComponent } from './page/profile-company.component';
import { ProfileCompanyRoutingModule } from './profile-company-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { TabsProfileCompanyComponent } from './components/tabs-profile-company/tabs-profile-company.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatGridListModule } from '@angular/material/grid-list';
import { FormsModule } from '@angular/forms';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';
import { InputProfilePersonalDataComponent } from './components/input-profile-personal-data/input-profile-personal-data.component';
import { InputProfileRegistryDataComponent } from './components/input-profile-registry-data/input-profile-registry-data.component';
import { InputProfileCompanyDataComponent } from './components/input-profile-company-data/input-profile-company-data.component';
import { InputProfileCompanyInformationComponent } from './components/input-profile-company-information/input-profile-company-information.component';

@NgModule({
  declarations: [
    ProfileCompanyComponent,
    TabsProfileCompanyComponent,
    InputProfilePersonalDataComponent,
    InputProfileRegistryDataComponent,
    InputProfileCompanyDataComponent,
    InputProfileCompanyInformationComponent
  ],
  imports: [
    CommonModule,
    ProfileCompanyRoutingModule,
    SharedModule,

    MatMenuModule,
    MatGridListModule,
    FormsModule,
    SetMaterialModule,
    MatIconModule,
    MatDividerModule,
    MatTabsModule

  ]
})
export class ProfileCompanyModule { }
