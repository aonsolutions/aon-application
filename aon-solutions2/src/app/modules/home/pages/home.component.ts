import { Component, OnInit, ViewChild } from '@angular/core';
import { CollectionFactory, ICollection } from 'libraries/AonSDK/src/aon';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { WorkingModalComponent } from '../components/working-modal/working-modal.component';
import { UploadModalComponent } from 'src/app/shared/components/file-upload-button/components/upload-modal/upload-modal.component';
import { ModalCreateComponent } from '../../inbox/components/modal-create/modal-create.component';
import { Router } from '@angular/router';

interface ShortcutDashboard {
  shape: string;
  name: string;
  modal: string;
}

interface ChartItem {
  name: string;
  typeDate: number;
}

interface MenuItems {
  routerlink: string;
  shape: string;
  name: string;
  class: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  shortcuts   : ShortcutDashboard[] = [];
  chartItems  : ChartItem[]         = [];
  menuItems   : MenuItems[]         = [];
  routerlink: string = '/inbox';
  public collectionFactory  = new CollectionFactory();
  @ViewChild('modal') modalComponent: any = '';

  constructor(
    private translateService: TranslateService,
    private router: Router,
  ) {
    this.translateService.get([
      'HOME.SALES_EXPENSES', 'HOME.COLLECTIONS_PAYMENTS', 'HOME.CREATE_INVOICE',
      'HOME.REGISTER_EMPLOYEE', 'HOME.CREATE_QUERY', 'HOME.TIMING', 'HOME.ASSESSMENT',
      'HOME.TAX_PANEL', 'HOME.EMPLOYEE_PANEL', 'HOME.DOCUMENTATION',
    ]).subscribe((result) => {
      this.shortcuts = [
        { shape: 'add_box'    , name: result['HOME.CREATE_INVOICE'], modal: 'workingModal' },
        { shape: 'person_add' , name: result['HOME.REGISTER_EMPLOYEE'], modal: 'workingModal' },
        { shape: 'add_comment', name: result['HOME.CREATE_QUERY'], modal: 'createQuery' },
        { shape: 'alarm'      , name: result['HOME.TIMING'], modal: 'workingModal' },
      ];
      this.chartItems = [
        {
          name      : result['HOME.SALES_EXPENSES'],
          typeDate  : 1, // Ventas/Gastos
        },
        {
          name      : result['HOME.COLLECTIONS_PAYMENTS'],
          typeDate  : 2, // Cobros/Pagos
        },
      ];
      this.menuItems = [
        {
          routerlink: '/billing',
          shape     : 'assessment',
          name      : result['HOME.ASSESSMENT'],
          class     : 'button-dashboard blue'
        },
        {
          routerlink: '/tax-panel',
          shape     : 'euro_symbol',
          name      : result['HOME.TAX_PANEL'],
          class     : 'button-dashboard orange'
        },
        {
          routerlink: '/employee-panel',
          shape     : 'people',
          name      : result['HOME.EMPLOYEE_PANEL'],
          class     : 'button-dashboard green'
        },
        {
          routerlink: '/documentation',
          shape     : 'description',
          name      : result['HOME.DOCUMENTATION'],
          class     : 'button-dashboard pink'
        },
      ];
    });
  }

  ngOnInit(): void {}

 // Modal para subir el archivo
  uploadDocument(event: Event) {
    event.preventDefault();
    this.modalComponent.openDialog(UploadModalComponent);
  }
  
  openModal(modalName: string) {
    switch (modalName) {
      case 'workingModal':
        this.workingModal();
        break;
        case 'createQuery':
        this.router.navigate([this.routerlink]);
        this.createQuery();
        break;
      default:
        break;
    }
  }
  
  createQuery() {
    this.modalComponent.openDialog(
      ModalCreateComponent
    );
  }
  
  workingModal() {
    this.modalComponent.openDialog(WorkingModalComponent);
  }
}
