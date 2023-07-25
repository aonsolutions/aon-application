import {  Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { MatFormFieldAppearance } from '@angular/material/form-field';

@Component({
  selector    : 'app-table',
  templateUrl : './table.component.html',
  styleUrls   : ['./table.component.scss']
})

export class TableComponent implements OnInit {
  @Input() displayedColumns : string[]  = ['name', 'weight', 'symbol', 'position'];
  columnsToDisplay          : string[]  = this.displayedColumns.slice();
  @Input() head             : any       = {name: 'Nombre', weight: 'valor 2', symbol: 'valor 3', position: 'valor 4'};
  @Input() data             : any       = [
    {position: 1,  name: 'Hydrogen',  weight: 1.0079,   symbol: 'H'},
    {position: 2,  name: 'Helium',    weight: 4.0026,   symbol: 'He'},
    {position: 3,  name: 'Lithium',   weight: 6.941,    symbol: 'Li'},
    {position: 4,  name: 'Beryllium', weight: 9.0122,   symbol: 'Be'},
    {position: 5,  name: 'Boron',     weight: 10.811,   symbol: 'B'},
    {position: 6,  name: 'Carbon',    weight: 12.0107,  symbol: 'C'},
    {position: 7,  name: 'Nitrogen',  weight: 14.0067,  symbol: 'N'},
    {position: 8,  name: 'Oxygen',    weight: 15.9994,  symbol: 'O'},
    {position: 9,  name: 'Fluorine',  weight: 18.9984,  symbol: 'F'},
    {position: 10, name: 'Neon',      weight: 20.1797,  symbol: 'Ne'},
  ];
  @Output() listenParentHandler : EventEmitter<any> = new EventEmitter();
  @Input()  pagination          : boolean           = false;
  
  ngOnInit(): void {
    this.columnsToDisplay = this.displayedColumns.slice();
  }

  tableClick(key: any, keyButton: any){
    this.listenParentHandler.emit({key: key, keyButton: keyButton});
  }

}
