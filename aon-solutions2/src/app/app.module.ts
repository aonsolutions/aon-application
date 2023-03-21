import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    SetMaterialModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
