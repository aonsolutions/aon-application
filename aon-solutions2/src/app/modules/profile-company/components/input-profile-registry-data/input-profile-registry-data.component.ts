import { Component, OnInit } from '@angular/core';
import { RegistryEnterpriseService } from 'src/app/core/services/registry-enterprise.service';

@Component({
  selector: 'app-input-profile-registry-data',
  templateUrl: './input-profile-registry-data.component.html',
  styleUrls: ['./input-profile-registry-data.component.scss']
})
export class InputProfileRegistryDataComponent implements OnInit {
  registryEnterprises: any[] = [];

  constructor(private registryEnterpriseService: RegistryEnterpriseService) { }

  ngOnInit(): void {
    this.registryEnterpriseService.getRegistryEnterprise('JyjNqGg3H1pfPak').then(registryEnterprise => {
      this.registryEnterprises.push(registryEnterprise);
    });
  }

}

//   ngOnInit() : void {
//     this.registryEnterpriseService.getRegistryEnterpriseList().then(registryEnterpriseCollection => {
//             this.registryEnterprises = registryEnterpriseCollection.toArray();

//           });
//         }


// }
