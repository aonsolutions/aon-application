import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { ModalEditTaxModelComponent } from '../modal-edit-tax-model/modal-edit-tax-model.component';
import { ModalPaymentComponent } from '../modal-payment/modal-payment.component';
import { ModalTaxesDetailsComponent } from '../modal-taxes-details/modal-taxes-details.component';
 import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-table-tax-model',
  templateUrl: './table-tax-model.component.html',
  styleUrls: ['./table-tax-model.component.scss'],
})
export class TableTaxModelComponent implements OnInit {
  @Input() trimester: number = 0;
  bodyTable: any = [];
  headerTable: any = {
     name: this.translateService.instant('TAX-PANEL.NAME'),
     result: this.translateService.instant('TAX-PANEL.RESULT'),
     status: this.translateService.instant('TAX-PANEL.STATUS'),
     paymentMethod: this.translateService.instant('TAX-PANEL.PAYMENTMETHOD'),
     actions: this.translateService.instant('TAX-PANEL.ACTIONS')
  };
  displayedColumns: string[] = [
    'name',
    'result',
    'status',
    'paymentMethod',
    'actions',
  ];

  constructor(public taxModelService: TaxModelService, private translateService: TranslateService) {
    let tableRow: any = [];
    let column: any = {};

    // Model date

    // selectedFields?: string[];
    // pageNum?: number;
    // pageItems?: number;
    // fields?: Map<string,any>;
    // intervalFields?: Map<string,any>;
    // orderBy?: Map<string,string>;

    taxModelService.getTaxModelList(
      {
        selectedFields:['trimester']
      }
    ).then((response) => {
      console.log(response)
      // Tax
      response.forEach(function (tax, taxKey) {
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

  @ViewChild('modalEdit') modalComponentEdit: any = '';
  @ViewChild('modalPayment') modalComponentPayment: any = '';
  @ViewChild('modalTaxesDetails') modalComponentTaxesDetails: any = '';

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  ngOnInit(): void {

  }

  modalClick(object: any) {
    // Fila de la tabla que se esta usando
    // Boton que ha sido clickeado
    console.log(object);
    // reference icon click
    console.log(object.keyButton);

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
    this.taxModelService.getTax(object.key).then((response) => {

    });
  }
}
