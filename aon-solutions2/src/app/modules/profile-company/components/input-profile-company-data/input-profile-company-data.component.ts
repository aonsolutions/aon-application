import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-input-profile-company-data',
  templateUrl: './input-profile-company-data.component.html',
  styleUrls: ['./input-profile-company-data.component.scss']
})
export class InputProfileCompanyDataComponent implements OnInit {
  emailFields: string[] = [];

  addEmailField() {
    this.emailFields.push(''); // Agrega un nuevo campo vacío a la lista
  }
  constructor() { }

  ngOnInit() {
    this.emailFields = ['', ''];
  }

}
