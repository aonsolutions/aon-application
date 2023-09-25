import {
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { ModalEditTaxModelComponent } from '../modal-edit-tax-model/modal-edit-tax-model.component';
import { ModalPaymentComponent } from '../modal-payment/modal-payment.component';
import { ModalTaxesDetailsComponent } from '../modal-taxes-details/modal-taxes-details.component';
import { FilterBuilder } from 'libraries/AonSDK/aon';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-table-tax-model',
  templateUrl: './table-tax-model.component.html',
  styleUrls: ['./table-tax-model.component.scss'],
})
export class TableTaxModelComponent implements OnChanges {
  @ViewChild('modalEdit') modalComponentEdit: any = '';
  @ViewChild('modalPayment') modalComponentPayment: any = '';
  @ViewChild('modalTaxesDetails') modalComponentTaxesDetails: any = '';
  @Input() trimester: number = 0;
  @Input() inputModel: number = 0;
  @Input() inputYear: string = '';
  headerTable: any = {};
  bodyTable: any = [];
  displayedColumns: string[] = [
    'name',
    'result',
    'status',
    'paymentMethod',
    'actions',
  ];
  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  constructor(
    public taxModelService: TaxModelService,
    private translateService: TranslateService
  ) {
    this.translateService
      .get([
        'TAX_PANEL.NAME',
        'TAX_PANEL.RESULT',
        'TAX_PANEL.STATUS',
        'TAX_PANEL.PAYMENTMETHOD',
        'TAX_PANEL.ACTIONS',
      ])
      .subscribe((result) => {
        this.headerTable = {
          name: result['TAX_PANEL.NAME'],
          result: result['TAX_PANEL.RESULT'],
          status: result['TAX_PANEL.STATUS'],
          paymentMethod: result['TAX_PANEL.PAYMENTMETHOD'],
          actions: result['TAX_PANEL.ACTIONS'],
        };
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    //Called before any other lifecycle hook. Use it to inject dependencies, but avoid any serious work here.
    //Add '${implements OnChanges}' to the class.
    this.updateTableData();
  }

  private updateTableData() {
    let filterBuilder = new FilterBuilder();
    if (this.trimester > 0) {
      filterBuilder.addField('trimester', +this.trimester);
    }

    if (this.inputModel > 0) {
      filterBuilder.addField('name', this.inputModel);
    }

    if (this.inputYear !== '') {
      filterBuilder.addField('year', +this.inputYear);
    }

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
      });
  }

  modalClick(object: any) {
    // Fila de la tabla que se esta usando
    // Boton que ha sido clickeado
    // console.log(object);
    // reference icon click
    // console.log(object.keyButton);

    switch (object.keyButton) {
      case 'edit':
        this.modalComponentEdit.openDialog(
          ModalEditTaxModelComponent,
          this.functionHome,
          'Data from home'
        );
        break;
      case 'done_all':
        this.modalComponentPayment.openDialog(
          ModalPaymentComponent,
          this.functionHome,
          'Data from home'
        );
        break;
      case 'eye':
        this.modalComponentTaxesDetails.openDialog(
          ModalTaxesDetailsComponent,
          this.functionHome,
          'Data from home'
        );
        break;
    }
    // Model tax reference
    this.taxModelService.getTax(object.key).then((response) => {});
  }
}
