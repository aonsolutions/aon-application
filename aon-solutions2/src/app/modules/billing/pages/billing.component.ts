import { Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, Factory, IBank, ICollection, IDocument } from 'libraries/AonSDK/src/aon';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';
import { SendFacturaComponent } from '../components/modal-send-factura/send-factura.component';
import { DuplicateFacturaComponent } from '../components/modal-duplicate-factura/duplicate-factura.component';
import { DeleteFacturaComponent } from '../components/modal-delete-factura/delete-factura.component';
import { ModalCreateComponent } from '../../inbox/components/modal-create/modal-create.component';
import { DocumentService } from 'src/app/core/services/document.service';
import { ResultSnackBarComponent } from 'src/app/shared/components/result-snack-bar/result-snack-bar.component';
import { MatSnackBar } from '@angular/material/snack-bar';

interface ChartItem {
  name: string;
  typeDate: number;
  select: number;
}

interface ChartReports {
  name: string;
  typeDate: number;
}

interface Tabs {
  name: string;
  icon?: string;
  color?: string;
}

@Component({
  selector: 'app-billing',
  templateUrl: './billing.component.html',
  styleUrls: ['./billing.component.scss'],
})
export class BillingComponent implements OnInit {
  chartItems: ChartItem[] = [];
  chartReports: ChartReports[] = [];
  selectedMenu: number = 1;
  selectedTab: number = 1;
  showMenu: boolean = false;
  buttonsVentas: any[] = [];
  buttonsGastos: any[] = [];
  buttonsEdit: any[] = [];
  addBank: boolean = false;
  banksView: any[] = [];
  editBank: { [key: number]: boolean } = {};
  salesSelected: any[] = [];
  tabs: Tabs[] = [];
  functionHome: any = (result: any) => this.afterModalClosed(result);

  @ViewChild('modal') modalComponent: any = '';

  public collectionFactory = new CollectionFactory();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );

  objectFactory = new Factory();
  document: IDocument = this.objectFactory.createDocument();

  constructor(
    private translateService: TranslateService,
    private bankService: BankService,
    private documentService: DocumentService,
    public snackBar: MatSnackBar
  ) {
    this.translateService
      .get([
        'BILLING.SALES',
        'BILLING.BILLS',
        'BILLING.REPORTS',
        'BILLING.SALES_CHECK',
      ])
      .subscribe((result) => {
        this.chartItems = [
          {
            name: result['BILLING.SALES'],
            typeDate: 1,
            select: 1,
          },
          {
            name: result['BILLING.BILLS'],
            typeDate: 2,
            select: 2,
          },
          {
            name: result['BILLING.REPORTS'],
            typeDate: 3,
            select: 3,
          },
        ];

        this.tabs = [
          {
            name: result['BILLING.SALES_CHECK'],
          },
        ];
      });

    this.chartReports = [
      {
        name: 'Ventas',
        typeDate: 1,
      },
    ];

    this.buttonsVentas = [
      {
        shape: 'aon_note_add',
        modal: '',
      },
      {
        shape: 'send',
        modal: 'showModalSend',
      },
      {
        shape: 'file_copy',
        modal: 'showModalCopy',
      },
      {
        shape: 'cloud_download',
        modal: '',
      },
      {
        shape: 'delete',
        modal: 'showModalDelete',
      },
    ];

    this.buttonsGastos = [
      {
        shape: 'picture_as_pdf',
      },
      {
        shape: 'excel',
      },
    ];

    this.buttonsEdit = [
      {
        shape: 'aon_note_add',
      },
      {
        shape: 'send',
      },
      {
        shape: 'remove_circle',
      },
      {
        shape: 'file_copy',
      },
      {
        shape: 'electronic_signature',
      },
      {
        shape: 'cloud_download',
      },
      {
        shape: 'delete',
      },
    ];

    this.banksView = [
      {
        id: 1,
        name: 'Caixabank',
        balance: '5.487,55',
        iban: 'ES123456789123456789',
        date: '30/05/2023',
        swift: 'CAIXESBBXXX',
        sync: 'sync',
      },
      {
        id: 2,
        name: 'Santander',
        balance: '8.887,02',
        iban: 'ES123456789123456789',
        date: '12/01/2023',
        swift: 'BSCHESMMXXX',
        sync: 'syncProblem',
      },
      {
        id: 3,
        name: 'Cajamar',
        balance: '1.125,54',
        iban: 'ES123456789123456789',
        date: '30/05/2023',
        swift: 'CCRIES2AXXX',
        sync: 'syncLost',
      },
    ];

    //bankService
    this.bankService.getBankList().then((response) => {
      this.banksSubject.next(response);
    });

  }

  afterModalClosed(result?: any) {
    console.log(result);
  }

  openModal(modal: string) {
    switch (modal) {
      case 'showModalSend':
        this.showModalSend();
        break;
      case 'showModalCopy':
        this.showModalCopy();
        break;
      case 'showModalDelete':
        this.showModalDelete();
        break;
      default:
        break;
    }
  }

  showModalSend() {
    this.modalComponent.openDialog(
      SendFacturaComponent,
      this.functionHome
    );
  }

  showModalCopy() {
    this.modalComponent.openDialog(
      DuplicateFacturaComponent,
      this.functionHome
    );
  }

  showModalDelete() {
    this.modalComponent.openDialog(
      DeleteFacturaComponent,
      this.functionHome
    );
  }

  editar(idBank: number) {
    this.editBank[idBank] = true;
  }

  guardar(idBank: number) {
    this.editBank[idBank] = false;
  }

  atras() {
    this.selectedTab = 4;
    this.selectedMenu = 2;
  }

  atrasVenta() {
    this.selectedTab = 1;
    this.selectedMenu = 1;
  }

  atrasGasto() {
    this.selectedTab = 2;
    this.selectedMenu = 1;
  }

  showSales(data: any) {
    // if (data[0].status) {
    //   this.selectedTab = 7;
    // } else {
    //   console.log('editar factura');
    // }
  }

  ngOnInit(): void {
    const url = window.location.href.split('/')[4];

    if (url === 'creacion') {
      this.selectedMenu = 2;
      this.selectedTab = 4;
      this.showMenu = true;
      this.addBank = true;
    }
  }
}
