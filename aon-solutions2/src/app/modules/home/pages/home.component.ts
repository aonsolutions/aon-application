import { Component, OnInit, ViewChild } from '@angular/core';
import {
  CollectionFactory,
  Factory,
  IBank,
  ICollection,
  IDocument,
} from 'libraries/AonSDK/src/aon';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { DocumentService } from '../../../core/services/document.service';
import { WorkingModalComponent } from '../components/working-modal/working-modal.component';
import { UploadModalComponent } from 'src/app/shared/components/file-upload-button/components/upload-modal/upload-modal.component';
import { ResultSnackBarComponent } from 'src/app/shared/components/result-snack-bar/result-snack-bar.component';
import { MatSnackBar } from '@angular/material/snack-bar';
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


  //banks area
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();

  @ViewChild('modal') modalComponent: any = '';
  objectFactory = new Factory();
  document: IDocument = this.objectFactory.createDocument();

  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );
  public banks$ = this.banksSubject.asObservable();

  constructor(
    private translateService: TranslateService,
    private documentService: DocumentService,
    private snackBar: MatSnackBar,
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

  // Acción que realiza al cerrar el modal
  functionHome: any = (result: any) => this.afterModalClosed(result);

  // Función que se ejecuta al cerrar el modal
  afterModalClosed(result: any) {
    // Si se ha seleccionado un documento
    if (result) {
      // Guardamos los datos del documento
      const fileName = result[0].document.name;
      const fileType = result[0].document.type;
      const fileSize = result[0].document.size;
      const path = result[0].folder;

      // Creamos el documento
      this.document = this.objectFactory.createDocument(
        result[0].document,
        fileName,
        fileSize,
        fileType,
        new Date(),
        path
      );
      const file = result[0].document;

      // Subimos el documento
      this.documentService
        .uploadDocument(this.document, file)
        .then((response) => {
          this.uploadCompletedModal();
        })
        .catch((error) => {
          this.uploadErrorModal();
        });
    }
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

  // Modal para cosas aún en construcción
  workingModal() {
    this.modalComponent.openDialog(WorkingModalComponent, this.functionHome);
  }

  createQuery() {
    this.modalComponent.openDialog(
      ModalCreateComponent,
      this.functionHome,
    );
  }

  // Confirmación de subida de documento
  uploadCompletedModal() {
    this.snackBar.openFromComponent(ResultSnackBarComponent, {
      data: {
        message: 'Documento subido correctamente',
        icon: 'check_circle',
        preClose: () => {
          this.snackBar.dismiss();
        },
      },
      panelClass: ['correct-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 3000,
    });
  }

  // Si la subida da error
  uploadErrorModal() {
    this.snackBar.openFromComponent(ResultSnackBarComponent, {
      data: {
        message: 'Error al subir el documento',
        icon: 'error',
        preClose: () => {
          this.snackBar.dismiss();
        },
      },
      panelClass: ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: 3000,
    });
  }

  showSelectFolder() {}

  // Modal para subir el archivo
  uploadDocument(event: Event) {
    event.preventDefault();
    this.modalComponent.openDialog(UploadModalComponent, this.functionHome);
  }

  ngOnInit(): void {}
}
