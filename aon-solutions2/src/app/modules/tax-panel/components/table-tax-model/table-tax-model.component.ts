import { Component, Input, OnInit } from '@angular/core';
import { TaxModel } from 'src/app/core/models/class/tax-model';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

@Component({
  selector    : 'app-table-tax-model',
  templateUrl : './table-tax-model.component.html',
  styleUrls   : ['./table-tax-model.component.scss']
})

export class TableTaxModelComponent implements OnInit {
  @Input() trimester!: number;
  dataSource         : any = [];
  headerTable        : any = {name: 'modelo', result: 'Resultado', status: 'estado', paymentMethod: 'Metodo de pago', actions: 'acciones'};
  displayedColumns   : string[] = ['name', 'result', 'status', 'paymentMethod', 'actions'];
  
  constructor( public taxModelService :TaxModelService ) {
    var tableData: any = [];
    // Model date
    taxModelService.getTaxModelList().then((response) => {
      // Tax
      response.forEach(function (tax, taxKey) {
        // Add object
        tableData[taxKey] = {};
        // Object Tax
        (Object.keys(tax) as (keyof typeof tax)[]).forEach((type, index) => {           
            // Predefinimos la key de la fila
            tableData[taxKey].key = taxKey
            // Cargamos datos en la tabla
            if(tax[type] === tax['name']){
              tableData[taxKey][type] = 
                "<div class='orange'>MODELO" + tax[type] + "</div>"+
                "<span class='griss'>"+ tax['taxType'] +"</span>";
            } else if(tax[type] === tax['result']){
              tableData[taxKey][type] = tax[type] + ' &euro;';
            } else if(tax[type] === tax['status']){
              tableData[taxKey][type] = "<span class='background-text-orange-light'>"+ tax[type] +"</span>";
              // Add date Actions ( buttons )
              // La referencia tendra que ser por ID, ya que el texto puede cambiar dependiendo del idioma
              switch(tax[type]){
                case 'pendiente':
                  tableData[taxKey].actions = ['eye', 'done_all', 'edit'];
                break;
                case 'confirmado':
                  tableData[taxKey].actions = ['eye'];
                break;
                case 'presentado':
                  tableData[taxKey].actions = ['picture_as_pdf'];
                break;
                default:
                  tableData[taxKey].actions = [];
                break;
              }
            } else {
              // Duplicate date
              tableData[taxKey][type] = tax[type];
            } 
        });
      }); 
      // Tax date format
      this.dataSource = tableData;
    });
  }

  ngOnInit(): void {
  }

  modalClick(object: any) {
    // Fila de la tabla que se esta usando
    // Boton que ha sido clickeado
    console.log(object);
    console.log(this.dataSource[object.key]);
    console.log(this.dataSource[object.key].actions[object.keyButton]);
  }

}
