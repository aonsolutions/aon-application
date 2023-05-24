import { Component, OnInit } from '@angular/core';
import { Enterprise } from 'src/app/shared/layouts/top-bar/top-bar.component';

@Component({
  selector: 'app-select-enterprise',
  templateUrl: './select-enterprise.component.html',
  styleUrls: ['./select-enterprise.component.scss']
})
export class SelectEnterpriseComponent implements OnInit {

  enterprises: Enterprise [] = [
    {name: 'Nombre de empresa 1'},
    {name: 'Nombre de empresa 2'},
    {name: 'Nombre de empresa 3'},
    {name: 'Nombre de empresa 4'},
  ];

  constructor() { }

  ngOnInit(): void {
  }

}
