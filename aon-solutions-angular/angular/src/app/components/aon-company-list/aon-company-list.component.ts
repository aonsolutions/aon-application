import { Component, OnInit, OnDestroy } from '@angular/core';
import { Company } from '../../models/AonModel';
import { CompanyService, SharedService, AonService } from '../../services/services';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'aon-company-list',
  templateUrl: './aon-company-list.component.html',
  styleUrls: ['./aon-company-list.component.css'],
})
export class AonCompanyListComponent implements OnInit, OnDestroy {
  companies: Company[] = [];

  constructor(public aonService: AonService, public service : SharedService, public companyService: CompanyService) {

  }

  companyFilter(f: Company) : boolean {
    const q = this.companyService.filter;
    let value = true;
    if(q.value) {
      const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
      const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
      value = document || name;
    }
    return value;
  }

  ngOnInit() {
    this.aonService.getCompanies().subscribe(
      (r: Company[]) => {

        this.companies = r;
      },
      (error: any) => alert(error)
    );
  }

  ngOnDestroy() {

  }

  onSelect(company: Company): void {
    this.service.company = company;
    localStorage.setItem('aon_domain_id', `${company.id}`);
    localStorage.setItem('aon_domain_name', company.domain);
    this.aonService.company = company;
    RootLoader.rootPanel('<aon-desktop></aon-desktop>');
  }

  public onScroll() {

  }
}
