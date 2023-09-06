import { Component, OnInit } from '@angular/core';
import { RegistryEnterpriseService } from 'src/app/core/services/registry-enterprise.service';

@Component({
  selector: 'app-input-profile-registry-data',
  templateUrl: './input-profile-registry-data.component.html',
  styleUrls: ['./input-profile-registry-data.component.scss'],
})
export class InputProfileRegistryDataComponent implements OnInit {
  registryEnterprises: any[] = [];

  constructor(private registryEnterpriseService: RegistryEnterpriseService) {}

  ngOnInit(): void {
    const documentEnt = localStorage.getItem('enterprise');
    this.registryEnterpriseService
      .getRegistryEnterprise(documentEnt)
      .then((registryEnterprise) => {
        this.registryEnterprises.push(registryEnterprise);
      });
  }
}

// ngOnInit(): void {
//   const documentEnt = localStorage.getItem('enterprise');
//   console.log('documentEnt', documentEnt);
//   const registryEnt = this.registryEnterpriseService.getRegistryEnterprise(documentEnt);

//   console.log('registryEnt', registryEnt);
// }
// }

//   ngOnInit() : void {
//     this.registryEnterpriseService.getRegistryEnterpriseList().then(registryEnterpriseCollection => {
//             this.registryEnterprises = registryEnterpriseCollection.toArray();

//           });
//         }

// }
