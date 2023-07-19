import { Component, Input } from '@angular/core';
import { TaxModel } from 'src/app/core/models/class/tax-model';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent {
  @Input() dataSource: TaxModel[] = [];
  displayedColumns: string[] = ['name', 'result', 'status', 'paymentMethod', 'acciones'];
type: any;


  @Input()
  trimester!: number;

}

