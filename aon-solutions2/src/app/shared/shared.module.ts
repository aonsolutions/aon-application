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
import { MatDividerModule } from '@angular/material/divider';
import { InputComponent } from './components/input/input.component';
import { ButtonComponent } from './components/button/button.component';
import { IconComponent } from './components/icon/icon.component';
import { MatButtonModule } from '@angular/material/button';
import { GlobalHoverDirective } from './directives/global-hover.directive';


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
    SidenavLayoutComponent,
    InputComponent,
    ButtonComponent,
    IconComponent,
    GlobalHoverDirective
  ],
  imports: [
    CommonModule,
    MatMenuModule,
    SetMaterialModule,
    MatSidenavModule,
    RouterModule,
    MatGridListModule,
    MatToolbarModule,
    MatDividerModule,
    MatButtonModule
  ],
  exports:[
    SidenavHoverDirective,
    SideNavComponent,
    TopBarComponent,
    BasicLayoutComponent,
    SidenavLayoutComponent,
    ContentComponent,
    ContentHeaderComponent,
    ContentMainComponent,
    ContentDetailComponent,
    ContentSideNavComponent,
    ListElementComponent,
    IconComponent,
    ButtonComponent
  ]
})
export class SharedModule { }
