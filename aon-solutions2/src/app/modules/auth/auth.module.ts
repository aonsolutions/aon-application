import { AuthRoutingModule } from './auth-routing.module';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NgModule } from '@angular/core';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from "../../shared/shared.module";
import { SelectEnterpriseComponent } from './components/select-enterprise/select-enterprise.component';


@NgModule({
    declarations: [
        LoginComponent,
        SelectEnterpriseComponent
    ],
    imports: [
        AuthRoutingModule,
        CommonModule,
        FormsModule,
        MatCardModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        SetMaterialModule,
        SharedModule,
    ]
})
export class AuthModule { }
