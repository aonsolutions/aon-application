import { Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, Factory, IBank, ICollection, IDocument } from 'libraries/AonSDK/src/aon';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';
import { SendFacturaComponent } from '../components/modal-send-factura/send-factura.component';
import { DuplicateFacturaComponent } from '../components/modal-duplicate-factura/duplicate-factura.component';
import { DeleteFacturaComponent } from '../components/modal-delete-factura/delete-factura.component';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { UploadModalComponent } from 'src/app/shared/components/file-upload-button/components/upload-modal/upload-modal.component';
import { DocumentService } from 'src/app/core/services/document.service';
import { ErrorModalComponent } from 'src/app/shared/components/error-modal/error-modal.component';
import { ConfirmModalComponent } from 'src/app/shared/components/confirm-modal/confirm-modal.component';

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
  showMenu           : boolean                    = false;
  buttonsVentas      : any[]                      = [];
  buttonsGastos      : any[]                      = [];
  buttonsEdit        : any[]                      = [];
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

  objectFactory = new Factory();
  document: IDocument = this.objectFactory.createDocument();

  constructor(
    private translateService: TranslateService,
    private bankService: BankService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private documentService: DocumentService
  ) {
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
  functionDocument: any = (result: any) => this.afterModalClosedDocuement(result);

  afterModalClosed(result?: any) {
    console.log(result);
  }

    // Modal para subir el archivo
  uploadDocument(event: Event) {
    event.preventDefault();
    this.modalComponent.openDialog(
      UploadModalComponent,
      this.functionDocument,
      'Data from home'
    );
  }

    // Al cerrar el modal de subir documento se crea el documento en la base de datos
  afterModalClosedDocuement(result?: any) {
    if (result) {
      const fileName = result[0].document.name;
      const fileType = result[0].document.type;
      const fileSize = result[0].document.size;
      const path     = result[0].folder;

      this.document = this.objectFactory.createDocument(result[0].document, fileName, fileSize, fileType, new Date(), path);
      const file = result[0].document;

      this.documentService.uploadDocument(this.document, file)
      .then((response) => {
        this.uploadCompletedModal()
      })
      .catch((error) => {
        this.uploadErrorModal()
      })

    }
  }

  // Confirmación de subida de documento
  uploadCompletedModal() {
    this.modalComponent.openDialog(
      ConfirmModalComponent,
      this.functionHome,
      'Documento subido correctamente'
    )
  }

  // Si la subida da error
  uploadErrorModal() {
    this.modalComponent.openDialog(
      ErrorModalComponent,
      this.functionHome,
      'Error al subir el documento'
    )
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

    if( url === 'creacion' ) {
      this.selectedMenu = 2;
      this.selectedTab = 4;
      this.showMenu = true;
      this.addBank = true;
    }

  }
}
