import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

export interface Tabs {
  name: string;
}

@Component({
  selector: 'app-tabs-profile-company',
  templateUrl: './tabs-profile-company.component.html',
  styleUrls: ['./tabs-profile-company.component.scss']
})

export class TabsProfileCompanyComponent implements OnInit {
  tabIndex: number = 0;
  tabs: Tabs[] = [];

  constructor(
    private translateService: TranslateService
  ) {
    this.translateService
      .get([
        'PROFILE.PERSONAL_INFORMATION',
        'PROFILE.COMPANY_INFORMATION',
        'PROFILE.REGISTRATION_INFORMATION',
        'PROFILE.CERTIFICATES'
      ])
      .subscribe((result) => {
        this.tabs = [
          { name: result['PROFILE.PERSONAL_INFORMATION'] },
          { name: result['PROFILE.COMPANY_INFORMATION'] },
          { name: result['PROFILE.REGISTRATION_INFORMATION'] },
          { name: result['PROFILE.CERTIFICATES'] },
        ];
      });
  }

  ngOnInit(): void {
  }
}
