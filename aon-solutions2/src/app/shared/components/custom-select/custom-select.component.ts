import { Component, Input, OnInit } from '@angular/core';
import { Asesor } from 'src/app/core/models/asesor';

@Component({
  selector: 'app-custom-select',
  templateUrl: './custom-select.component.html',
  styleUrls: ['./custom-select.component.scss']
})
export class CustomSelectComponent implements OnInit {

  @Input() asesores: Asesor [] = [];
  selected: Asesor = new Asesor;

  constructor() { }

  ngOnInit(): void {
    this.selected = this.asesores[0];
  }

}
