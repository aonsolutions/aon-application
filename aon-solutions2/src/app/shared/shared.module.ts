import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AccordionComponent } from './components/accordion/accordion.component';
import { BasicLayoutComponent } from './layouts/basic-layout/basic-layout.component';
import { BreadcumbComponent } from './components/breadcumb/breadcumb.component';
import { ButtonComponent } from './components/button/button.component';
import { CardComponent } from './components/card/card.component';
import { ChartComponent } from './components/chart/chart.component';
import { ChartsModule } from 'ng2-charts';
import { ContentComponent } from './layouts/content/content.component';
import { ContentDetailComponent } from './layouts/content-detail/content-detail.component';
import { ContentHeaderComponent } from './layouts/content-header/content-header.component';
import { ContentMainComponent } from './layouts/content-main/content-main.component';
import { ContentSideNavComponent } from './layouts/content-side-nav/content-side-nav.component';
import { DragAndDropDirective } from './directives/drag-and-drop.directive';
import { DropdownMenuComponent } from './components/dropdown-menu/dropdown-menu.component';
import { FileUploadButtonComponent } from './components/file-upload-button/file-upload-button.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GlobalHoverDirective } from './directives/global-hover.directive';
import { GridColsDirective } from './directives/grid-cols.directive';
import { GridLayoutComponent } from './layouts/grid-layout/grid-layout.component';
import { IconComponent } from './components/icon/icon.component';
import { InputComponent } from './components/input/input.component';
import { ListElementComponent } from './layouts/list-element/list-element.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { ModalControllerComponent } from './components/modal-controller/modal-controller.component';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SideNavComponent } from './layouts/side-nav/side-nav.component';
import { SidenavHoverDirective } from './directives/sidenav-hover.directive';
import { SidenavLayoutComponent } from './layouts/sidenav-layout/sidenav-layout.component';
import { TabsComponent } from './components/tabs/tabs.component';
import { TagComponent } from './components/tag/tag.component';
import { TopBarComponent } from './layouts/top-bar/top-bar.component';
import { FileViewerComponent } from './components/file-viewer/file-viewer.component';
import { PdfJsViewerModule } from 'ng2-pdfjs-viewer';
import { ErrorSnackBarComponent } from './components/error-snack-bar/error-snack-bar.component';
import { TableComponent } from './components/table/table.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';

@NgModule({
  declarations: [
    AccordionComponent,
    BasicLayoutComponent,
    BreadcumbComponent,
    ButtonComponent,
    CardComponent,
    ChartComponent,
    ContentComponent,
    ContentDetailComponent,
    ContentHeaderComponent,
    ContentMainComponent,
    ContentSideNavComponent,
    DragAndDropDirective,
    DropdownMenuComponent,
    FileUploadButtonComponent,
    GlobalHoverDirective,
    GridColsDirective,
    GridLayoutComponent,
    IconComponent,
    InputComponent,
    ListElementComponent,
    ModalControllerComponent,
    SideNavComponent,
    SidenavHoverDirective,
    SidenavLayoutComponent,
    TabsComponent,
    TagComponent,
    TopBarComponent,
    FileViewerComponent,
    ErrorSnackBarComponent,
    TableComponent,
  ],
  imports: [
    ChartsModule,
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatMenuModule,
    MatMenuModule,
    MatSelectModule,
    MatSidenavModule,
    MatTabsModule,
    MatToolbarModule,
    ReactiveFormsModule,
    RouterModule,
    SetMaterialModule,
    PdfJsViewerModule,
    MatTableModule,
    MatSortModule
  ],
  exports:[
    AccordionComponent,
    BasicLayoutComponent,
    ButtonComponent,
    CardComponent,
    ChartComponent,
    ContentComponent,
    ContentDetailComponent,
    ContentHeaderComponent,
    ContentMainComponent,
    ContentSideNavComponent,
    DropdownMenuComponent,
    FileUploadButtonComponent,
    GridColsDirective,
    GridLayoutComponent,
    IconComponent,
    InputComponent,
    ListElementComponent,
    ModalControllerComponent,
    SideNavComponent,
    SidenavHoverDirective,
    SidenavLayoutComponent,
    TabsComponent,
    TagComponent,
    TopBarComponent,
    FileViewerComponent
  ]
})
export class SharedModule { }
