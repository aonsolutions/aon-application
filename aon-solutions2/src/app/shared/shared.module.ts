import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';
import { SidenavHoverDirective } from './directives/sidenav-hover.directive';
import { SideNavComponent } from './layouts/side-nav/side-nav.component';
import { TopBarComponent } from './layouts/top-bar/top-bar.component';
import { ContentComponent } from './layouts/content/content.component';
import { ContentHeaderComponent } from './layouts/content-header/content-header.component';
import { ContentMainComponent } from './layouts/content-main/content-main.component';
import { ContentDetailComponent } from './layouts/content-detail/content-detail.component';
import { ContentSideNavComponent } from './layouts/content-side-nav/content-side-nav.component';
import { ListElementComponent } from './layouts/list-element/list-element.component';
import { BasicLayoutComponent } from './layouts/basic-layout/basic-layout.component';
import { SidenavLayoutComponent } from './layouts/sidenav-layout/sidenav-layout.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterModule } from '@angular/router';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatToolbarModule } from '@angular/material/toolbar';


@NgModule({
  declarations: [
    SidenavHoverDirective,
    SideNavComponent,
    TopBarComponent,
    ContentComponent,
    ContentHeaderComponent,
    ContentMainComponent,
    ContentDetailComponent,
    ContentSideNavComponent,
    ListElementComponent,
    BasicLayoutComponent,
    SidenavLayoutComponent
  ],
  imports: [
    CommonModule,
    MatMenuModule,
    SetMaterialModule,
    MatSidenavModule,
    RouterModule,
    MatGridListModule,
    MatToolbarModule
  ],
  exports:[
    SidenavHoverDirective,
    SideNavComponent,
    TopBarComponent,
    BasicLayoutComponent,
    SidenavLayoutComponent
  ]
})
export class SharedModule { }
