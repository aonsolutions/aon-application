import { Component, Input, OnInit } from '@angular/core';
import { IEnterprise } from 'libraries/AonSDK/src/aon';

@Component({
  selector: 'app-input-profile-company-data',
  templateUrl: './input-profile-company-data.component.html',
  styleUrls: ['./input-profile-company-data.component.scss'],
})
export class InputProfileCompanyDataComponent implements OnInit {
  @Input() enterprise: IEnterprise | null = null;
  emailList: string[] = ['', ''];
  addForm: number = 0;
  addMail: number = 0;

  constructor() {
  }

  async ngOnInit() {
  }

  addEmail() {
    this.emailList.push('');
  }

}
