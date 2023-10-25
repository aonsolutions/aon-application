import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-create-new-contract',
  templateUrl: './create-new-contract.component.html',
  styleUrls: ['./create-new-contract.component.scss'],
})
export class CreateNewContractComponent implements OnInit {
  @Output() changeTabIndex: EventEmitter<number> = new EventEmitter<number>();

  constructor() {}

  ngOnInit() {}
  paginaAtras() {
    const tabIndex = 0;
    this.changeTabIndex.emit(tabIndex);
  }
}
