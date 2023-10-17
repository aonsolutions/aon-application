import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-factura',
  templateUrl: './factura.component.html',
  styleUrls: ['./factura.component.scss'],
})
export class FacturaComponent implements OnInit {
  optionsSerie: any = {};
  optionsCategory: any = {};
  optionsIVA: any = {};
  optionsIRPF: any = {};
  optionsTransaction: any = {};
  optionsActivity: any = {};
  optionsPay: any = {};
  buttonsFacturaVenta: any[] = [];

  @Input() titulo = '';

  @Output() atras = new EventEmitter<any>();

  constructor() {
    this.buttonsFacturaVenta = [
      {
        shape: 'note_add',
      },
      {
        shape: 'send',
      },
      {
        shape: 'file_copy',
      },
      {
        shape: 'delete',
      },
    ];
  }

  ngOnInit(): void {}

  paginaAtras() {
    this.atras.emit();
  }
}
