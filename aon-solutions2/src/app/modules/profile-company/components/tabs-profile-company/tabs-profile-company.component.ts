import { Component, OnInit } from '@angular/core';


export interface Tabs {
  name: string;
}
@Component({
  selector: 'app-tabs-profile-company',
  templateUrl: './tabs-profile-company.component.html',
  styleUrls: ['./tabs-profile-company.component.scss']
})
export class TabsProfileCompanyComponent implements OnInit {
  tabIndex:number = 0
  constructor() { }

  tabs: Tabs[] = [
    { name: 'Datos personales' },
    { name: 'Datos de la empresa' },
    { name: 'Datos registrales' },
    { name: 'Certificados' }
  ];
  ngOnInit(): void {
  }

}
