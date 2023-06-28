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
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { DropdownMenuComponent } from './components/dropdown-menu/dropdown-menu.component';
import { BreadcumbComponent } from './components/breadcumb/breadcumb.component';
import { TabsComponent } from './components/tabs/tabs.component';
import { MatTabsModule } from '@angular/material/tabs';
import { FileUploadButtonComponent } from './components/file-upload-button/file-upload-button.component';
import { DragAndDropDirective } from './directives/drag-and-drop.directive';
import { TagComponent } from './components/tag/tag.component';
import { CardComponent } from './components/card/card.component';
import { AccordionComponent } from './components/accordion/accordion.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { ModalControllerComponent } from './components/modal-controller/modal-controller.component';
import { MatDialogModule } from '@angular/material/dialog';
import { PaginationContainerComponent } from './components/pagination-container/pagination-container.component';
import { PaginationControllerComponent } from './components/pagination-controller/pagination-controller.component';
import { GridLayoutComponent } from './layouts/grid-layout/grid-layout.component';
import { GridColsDirective } from './directives/grid-cols.directive';
import { ChartsModule } from 'ng2-charts';
import { ChartComponent } from './components/chart/chart.component';


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
    GlobalHoverDirective,
    DropdownMenuComponent,
    BreadcumbComponent,
    TabsComponent,
    FileUploadButtonComponent,
    DragAndDropDirective,
    TagComponent,
    CardComponent,
    AccordionComponent,
    ModalControllerComponent,
    PaginationContainerComponent,
    PaginationControllerComponent,
    GridLayoutComponent,
    GridColsDirective,
    ChartComponent
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
    MatButtonModule,
    MatInputModule,
    FormsModule,
    MatIconModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatSelectModule,
    MatMenuModule,
    MatTabsModule,
    MatExpansionModule,
    MatDialogModule,
    ChartsModule
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
    ButtonComponent,
    InputComponent,
    DropdownMenuComponent,
    TabsComponent,
    FileUploadButtonComponent,
    TagComponent,
    CardComponent,
    AccordionComponent,
    ModalControllerComponent,
    PaginationContainerComponent,
    PaginationControllerComponent,
    GridLayoutComponent,
    GridColsDirective,
    ChartComponent
  ]
})
export class SharedModule { }
