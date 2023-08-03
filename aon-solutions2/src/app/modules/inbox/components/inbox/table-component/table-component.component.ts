import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-table-component',
  templateUrl: './table-component.component.html',
  styleUrls: ['./table-component.component.scss'],
})

export class TableComponentComponent implements OnInit {
  displayedColumns   : string[] = ['name', 'result', 'status', 'paymentMethod', 'actions'];
  constructor() { }

  ngOnInit(): void {
  }

}
