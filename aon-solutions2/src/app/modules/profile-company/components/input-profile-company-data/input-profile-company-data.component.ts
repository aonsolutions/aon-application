import { Component, OnInit } from '@angular/core';
import { EnterpriseService } from '../../../../core/services/enterprise.service';

@Component({
  selector: 'app-input-profile-company-data',
  templateUrl: './input-profile-company-data.component.html',
  styleUrls: ['./input-profile-company-data.component.scss'],
})
export class InputProfileCompanyDataComponent implements OnInit {
  enterprises: any[] = [];

  constructor(private enterpriseService: EnterpriseService) {}

    ngOnInit(): void {
      this.enterpriseService.getEnterprise('B16880148').then(enterprise => {
        this.enterprises.push(enterprise);
      });
      console.log("tab", this.enterprises);
    }
  }

//   ngOnInit(): void {
//     this.enterpriseService.getEnterpriseList().then((enterpriseCollection) => {
//       this.enterprises = enterpriseCollection.toArray();
//       console.log(this.enterprises);
//     });
//   }
// }
