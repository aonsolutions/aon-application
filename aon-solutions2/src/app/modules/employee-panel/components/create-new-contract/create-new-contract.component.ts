import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-create-new-contract',
  templateUrl: './create-new-contract.component.html',
  styleUrls: ['./create-new-contract.component.scss']
})
export class CreateNewContractComponent implements OnInit {
  @Output() atras = new EventEmitter<any>();
  constructor() { }

  ngOnInit() {
  }
  paginaAtras() {
    this.atras.emit();
  }

}
