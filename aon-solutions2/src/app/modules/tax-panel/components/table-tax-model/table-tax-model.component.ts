import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { ModalEditTaxModelComponent } from '../modal-edit-tax-model/modal-edit-tax-model.component';
import { ModalPaymentComponent } from '../modal-payment/modal-payment.component';
import { ModalTaxesDetailsComponent } from '../modal-taxes-details/modal-taxes-details.component';
import { FilterBuilder } from 'libraries/AonSDK/aon';

@Component({
  selector: 'app-table-tax-model',
  templateUrl: './table-tax-model.component.html',
  styleUrls: ['./table-tax-model.component.scss'],
})
export class TableTaxModelComponent implements OnInit {

  @Input() trimester: number = 0;
  bodyTable: any = [];
  headerTable: any = {
    name: 'modelo',
    result: 'Resultado',
    status: 'estado',
    paymentMethod: 'Metodo de pago',
    actions: 'acciones',
  };
  displayedColumns: string[] = [
    'name',
    'result',
    'status',
    'paymentMethod',
    'actions',
  ];

  constructor(public taxModelService: TaxModelService) {}

  ngOnInit(): void {
    this.updateTableData();
    console.log(this.trimester);
  }

  private updateTableData() {
    let filterBuilder = new FilterBuilder();
    filterBuilder.addField('trimester', this.trimester);
    this.taxModelService.getTaxModelList(filterBuilder.getFilter()).then((response) => {
      console.log(response);

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

  @ViewChild('modalEdit') modalComponentEdit: any = '';
  @ViewChild('modalPayment') modalComponentPayment: any = '';
  @ViewChild('modalTaxesDetails') modalComponentTaxesDetails: any = '';

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

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
      // ...
    });
  }
}
