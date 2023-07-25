import { Component, Input, OnInit } from '@angular/core';
import { CollectionFactory, ICollection, ITaxModel } from 'libraries/AonSDK/aon';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

@Component({
  selector    : 'app-table-tax-model',
  templateUrl : './table-tax-model.component.html',
  styleUrls   : ['./table-tax-model.component.scss']
})

export class TableTaxModelComponent implements OnInit {
  @Input() trimester!: number;
  bodyTable          : any = [];
  headerTable        : any = {name: 'modelo', result: 'Resultado', status: 'estado', paymentMethod: 'Metodo de pago', actions: 'acciones'};
  displayedColumns   : string[] = ['name', 'result', 'status', 'paymentMethod', 'actions'];

  constructor( public taxModelService :TaxModelService ) {
    let tableRow : any = [];
    let column   : any = {};
    // Model date
    taxModelService.getTaxModelList().then((response) => {
      // Tax
      response.forEach(function (tax, taxKey) {
        // clone object
        column = Object.assign({}, tax);
        // Object Tax
          // Predefinimos la key de la fila
          column.key  = taxKey;
          // Cargamos datos en la tabla
          column.name =
              "<div class='orange'>MODELO" + tax.Name + "</div>"+
              "<span class='griss'>"+ tax.TaxType +"</span>";
          column.result = tax.Result + ' &euro;';
          column.status = "<span class='background-text-orange-light'>"+ tax.Status +"</span>";
          // Add date Actions ( buttons )
          // La referencia tendra que ser por ID, ya que el texto puede cambiar dependiendo del idioma
          switch(tax.Status){
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



  ngOnInit(): void {
  }

  modalClick(object: any) {
    // Fila de la tabla que se esta usando
    // Boton que ha sido clickeado
    console.log(object);
    // reference icon click
    console.log(object.keyButton);
    // Model tax reference
    this.taxModelService.getTax(object.key).then((response) => {
      console.log(response);
    });
  }

}
