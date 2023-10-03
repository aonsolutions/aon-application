import { Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, IBank, ICollection } from 'libraries/AonSDK/src/aon';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';
import { SendFacturaComponent } from '../components/send-factura/send-factura.component';
import { DuplicateFacturaComponent } from '../components/duplicate-factura/duplicate-factura.component';
import { DeleteFacturaComponent } from '../components/delete-factura/delete-factura.component';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { SetproductIcons } from 'libraries/setproduct-icons';

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
  chartItems         : ChartItem[]                = [];
  chartReports       : ChartReports[]             = [];
  selectedMenu       : number                     = 1;
  selectedTab        : number                     = 1;
  showMenu           : boolean                    = true;
  buttonsVentas      : any[]                      = [];
  buttonsGastos      : any[]                      = [];
  buttonsFacturaVenta: any[]                      = [];
  buttonsEdit        : any[]                      = [];
  optionsSerie       : any                        = {};
  optionsCategory    : any                        = {};
  optionsIVA         : any                        = {};
  optionsIRPF        : any                        = {};
  optionsTransaction : any                        = {};
  optionsActivity    : any                        = {};
  optionsPay         : any                        = {};
  addBank            : boolean                    = false;
  banksView          : any[]                      = [];
  editBank           : { [key: number]: boolean } = {};
  salesSelected      : any[]                      = [];
  tabs               : Tabs[]                     = [];

  @ViewChild('modal') modalComponent: any = '';

  public collectionFactory = new CollectionFactory();
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );

  public banks$ = this.banksSubject.asObservable();

  constructor(
    private translateService: TranslateService,
    private bankService: BankService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
  ) {

    SetproductIcons.add({
      "add_note":
      `<svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24"><path d="M440-240h80v-120h120v-80H520v-120h-80v120H320v80h120v120ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z"/></svg>`
    })

    console.log(SetproductIcons.get('add_note'));

    this.translateService
      .get(['BILLING.SALES', 'BILLING.BILLS', 'BILLING.REPORTS', 'BILLING.SALES_CHECK'])
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
          }
        ]

      });

    this.chartReports = [
      {
        name: 'Ventas',
        typeDate: 1,
      },
    ];

    this.buttonsVentas = [
      {
        shape: 'add_note',
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
        shape: 'add_note',
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

    this.buttonsFacturaVenta = [
      {
        shape: 'note_add',
      },
      {
        shape: 'send',
      },
      {
        shape: 'file_copy',
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
        iban: 'ES12 3456 7891 2345 6789',
        date: '30/05/2023',
        swift: 'CAIXESBBXXX',
        sync: 'sync',
      },
      {
        id: 2,
        name: 'Santander',
        balance: '8.887,02',
        iban: 'ES12 3456 7891 2345 6789',
        date: '12/01/2023',
        swift: 'BSCHESMMXXX',
        sync: 'syncProblem',
      },
      {
        id: 3,
        name: 'Cajamar',
        balance: '1.125,54',
        iban: 'ES12 3456 7891 2345 6789',
        date: '30/05/2023',
        swift: 'CCRIES2AXXX',
        sync: 'syncLost',
      },
    ];

    //bankService
    this.bankService.getBankList().then((response) => {
      this.banks = response;
      this.banksSubject.next(this.banks);
    });

    //Iconos personalizados
    this.matIconRegistry.addSvgIcon(
      'add-note',
      this.domSanitizer.bypassSecurityTrustResourceUrl(
        '../../../../assets/images/note_add.svg'
      )
    )

  }

  functionHome: any = (result: any) => this.afterModalClosed(result);

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
      this.functionHome,
      'Data from home'
    );
  }

  showModalCopy() {
    this.modalComponent.openDialog(
      DuplicateFacturaComponent,
      this.functionHome,
      'Data from home'
    );
  }

  showModalDelete() {
    this.modalComponent.openDialog(
      DeleteFacturaComponent,
      this.functionHome,
      'Data from home'
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

  showSales(data: any) {

    console.log(data);

    // if (data[0].status) {

    //   this.selectedTab = 7;

    // } else {

    //   console.log('editar factura');

    // }
  }

  ngOnInit(): void {}
}
