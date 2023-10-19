import { Component, ViewChild, HostBinding, Input, OnInit} from '@angular/core';
import { ModalEditTaxModelComponent } from '../components/modal-edit-tax-model/modal-edit-tax-model.component';
import { ModalPaymentComponent } from '../components/modal-payment/modal-payment.component';
import { ModalTaxesDetailsComponent } from '../components/modal-taxes-details/modal-taxes-details.component';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { TranslateService } from '@ngx-translate/core';
import { FilterBuilder } from 'libraries/AonSDK/src/aon';
export interface Tabs {
  name: string; // Nombre de la tab
  icon?: string; // Icono opcional de la tab
  color?: string; // Color del texto y del icono de la tab
}
export interface Models {
  name: string;
  taxType: number;
  result: number;
  status: string;
  paymentMethod?: string;
  trimester: number;
  year: number;
  acciones?: any;
}
@Component({
  selector: 'app-taxPanel',
  templateUrl: './taxPanel.component.html',
  styleUrls: ['./taxPanel.component.scss'],
})
export class TaxPanelComponent implements  OnInit {
  @Input() tabColor: string = '';
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @ViewChild('modalEdit') modalComponentEdit: any = '';
  @ViewChild('modalPayment') modalComponentPayment: any = '';
  @ViewChild('modalTaxesDetails') modalComponentTaxesDetails: any = '';
  tabIndex: number = 0;
  modelsList: any[] = [];
  modelsYears: any[] = [];
  models: any;
  selectedModel: any = '0';
  currentDate = new Date();
  selectedYear: string = this.currentDate.getFullYear().toString();
  spinner: boolean = true;
  showModal: any;
  headerTable: any = {};
  bodyTable: any = [];
  displayedColumns: string[] = [
    'name',
    'result',
    'status',
    'paymentMethod',
    'actions',
  ];
  statusCrudo: string = '';

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  tabs: Tabs[] = [
    { name: 'tab1', icon: 'create', color: 'black' },
    { name: 'tab2', icon: 'delete', color: 'black' },
    { name: 'tab3', color: 'black' },
    { name: 'todos', color: 'black' },
  ];

  constructor(
    private taxModelService: TaxModelService,
    private translateService: TranslateService
  ) {
    this.translateService
      .get([
        'TAX_PANEL.1_TRIMESTER',
        'TAX_PANEL.2_TRIMESTER',
        'TAX_PANEL.3_TRIMESTER',
        'TAX_PANEL.4_TRIMESTER',
        'TAX_PANEL.ALL',
        'TAX_PANEL.NAME',
        'TAX_PANEL.RESULT',
        'TAX_PANEL.STATUS',
        'TAX_PANEL.PAYMENTMETHOD',
        'TAX_PANEL.ACTIONS',
      ])
      .subscribe((result) => {
        // Marcar el trimestre en el que estamos
        taxModelService.thisTrimester().then((response) => {
          // Le restamos 1 para que coincida con el valor del Tabs
          this.tabIndex = response - 1;
        });
        // Cabecera de los tabs
        this.tabs = [
          { name: result['TAX_PANEL.1_TRIMESTER'] },
          { name: result['TAX_PANEL.2_TRIMESTER'] },
          { name: result['TAX_PANEL.3_TRIMESTER'] },
          { name: result['TAX_PANEL.4_TRIMESTER'] },
          { name: result['TAX_PANEL.ALL'] },
        ];
        this.headerTable = {
          name: result['TAX_PANEL.NAME'],
          result: result['TAX_PANEL.RESULT'],
          status: result['TAX_PANEL.STATUS'],
          paymentMethod: result['TAX_PANEL.PAYMENTMETHOD'],
          actions: result['TAX_PANEL.ACTIONS'],
        };
      });

    // Datos del modelo
    taxModelService.getTaxModelList().then((response) => {
      this.spinner = false;
      this.models = response;
      this.translateService
        .get('TAX_PANEL.ALL_MODELS')
        .subscribe((translatedText) => {
          this.modelsList.push({ value: 0, text: translatedText });
        });

      response.forEach((element) => {
        if (!this.modelsList.some((model) => model.text === element.Name)) {
          this.modelsList.push({ value: element.Name, text: element.Name });
        }
        if (!this.modelsYears.some((model) => model.text === element.Year)) {
          this.modelsYears.push({ value: element.Year, text: element.Year });
        }
      });
      this.updateTableData();
    });
  }

  ngOnInit(): void {
    this.updateTableData();
  }

  changeTabIndex(index: number) {
    this.tabIndex = index;
    this.updateTableData();
  }

  filterModel(value: number, type: number) {
    this.selectedModel = type === 1 ? value : this.selectedModel;
    this.selectedYear = type === 2 ? value.toString() : this.selectedYear;

    this.updateTableData();
  }

  private updateTableData() {
    let filterBuilder = new FilterBuilder();
    if (this.tabIndex < 4) {
      filterBuilder.addField('trimester', +this.tabIndex + 1);
    }

    if (this.selectedModel > 0) {
      filterBuilder.addField('name', this.selectedModel);
    }

    filterBuilder.addField('year', +this.selectedYear);

    this.taxModelService
      .getTaxModelList(filterBuilder.getFilter())
      .then((response) => {
        // Tax
        let tableRow: any = [];
        response.forEach(function (tax, taxKey) {
          let column: any = {};
          // clone object
          column = Object.assign({}, tax);
          // Object Tax
          // Predefinimos la key de la fila
          column.key = taxKey;
          // Cargamos datos en la tabla
          column.name =
            "<div class='orange'>MODELO " +
            tax.Name +
            '</div>' +
            "<span class='griss'>" +
            tax.TaxType +
            '</span>';
          column.result = tax.Result + ' &euro;';
          column.status =
            tax.Status === 'pendiente'
              ? {
                  icon: [{ watch_later: 'orange' }],
                  text:
                    "<span class='background-text-orange'>" +
                    tax.Status +
                    '</span>',
                }
              : "<span class='background-text-orange-light margin-left-2'>" +
                tax.Status +
                '</span>';
          // Add date Actions ( buttons )
          // La referencia tendra que ser por ID, ya que el texto puede cambiar dependiendo del idioma
          switch (tax.Status) {
            case 'pendiente':
              column.actions = ['eye', 'done_all', 'edit'];
              break;
            case 'confirmado':
              column.actions = ['eye'];
              break;
            case 'presentado':
              column.actions = ['picture_as_pdf'];
              break;
            default:
              column.actions = [];
              break;
          }
          // Add object date table
          tableRow.push(column);
        });
        // Tax date format for table
        this.bodyTable = tableRow;

        // No tenemos modelos en la tabla
        if (response.size() === 0) {
          this.translateService
            .get(['TAX_PANEL.NO_MODELS'])
            .subscribe((result) => {
              this.models = result['TAX_PANEL.NO_MODELS'];
            });
        }
      });
  }
  receiveStatus($event: string) {
    this.statusCrudo = $event;
  }
  showModalEdit() {
    this.modalComponentEdit.openDialog(
      ModalEditTaxModelComponent,
      this.functionHome,
      'Data from home'
    );
  }
  showModalPayment() {
    this.modalComponentPayment.openDialog(
      ModalPaymentComponent,
      this.functionHome,
      'Data from home'
    );
  }
  showModalTaxesDetails() {
    this.modalComponentTaxesDetails.openDialog(
      ModalTaxesDetailsComponent,
      this.functionHome,
      'Data from home'
    );
  }
  modalClick(object: any) {
    // Fila de la tabla que se esta usando
    // Boton que ha sido clickeado
    // reference icon click
    switch (object.keyButton) {
      case 'edit':
        this.modalComponentEdit.openDialog(
          ModalEditTaxModelComponent,
          this.functionHome,
          object
        );
        break;
      case 'done_all':
        this.modalComponentPayment.openDialog(
          ModalPaymentComponent,
          this.functionHome,
          object
        );

        break;
      case 'eye':
        this.modalComponentTaxesDetails.openDialog(
          ModalTaxesDetailsComponent,
          this.functionHome,
          object
        );
        break;
    }
    // Model tax reference
    this.taxModelService.getTax(object.key).then((response) => {});
  }
}
