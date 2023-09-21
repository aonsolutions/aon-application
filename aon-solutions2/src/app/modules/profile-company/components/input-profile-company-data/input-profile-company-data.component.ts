import { Component, OnInit } from '@angular/core';
import { EnterpriseService } from '../../../../core/services/enterprise.service';

@Component({
  selector: 'app-input-profile-company-data',
  templateUrl: './input-profile-company-data.component.html',
  styleUrls: ['./input-profile-company-data.component.scss'],
})
export class InputProfileCompanyDataComponent implements OnInit {
  enterprises: any[] = [];
  emailList: string[] = ['', ''];
  addForm: number = 0;
  addMail: number = 0;

  constructor(private enterpriseService: EnterpriseService) {
    this.enterpriseService.getEnterprise('B16880148').then((enterprise) => {
      this.enterprises.push(enterprise);
    });
  }

  ngOnInit(): void {

  }

  addEmail() {
    this.emailList.push('');
  }

}
