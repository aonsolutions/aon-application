import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Company, CompanyFilter } from '../../models/models';
import { CompanyService, SharedService, AonService } from '../../services/services';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'aon-company-list',
  templateUrl: './aon-company-list.component.html',
  styleUrls: ['./aon-company-list.component.css'],
})
export class AonCompanyListComponent implements OnInit, OnDestroy {
  companies: Company[] = [];

  constructor(private router: Router, public aonService: AonService, public service : SharedService, public companyService: CompanyService) {
    this.companyService.filterObservable.subscribe( (value: CompanyFilter) => {
      if(this.aonService.companies && this.aonService.companies.length > 0){
        this.companies = this.aonService.companies.filter(f => this.companyFilter(f));
      }
    });
  }

  companyFilter(f: Company) : boolean {
    if(!this.companyService.filter) {
      this.companyService.filter = {
        inactive: false,
        active: true,
        shared: true
      };
    }
    const q = this.companyService.filter;
    let value = true;
    if(q && q.value) {
      const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
      const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
      value = document || name;
    }

    if(q && q.active && !q.inactive) {
      value = f.active && value;
    }

    if(q && q.inactive && !q.active) {
      value = !f.active && value;
    }

    if(q && q.shared) {
      // TODO
    }

    return value;
  }

  ngOnInit() {
    this.init();
  }

  init() {
    this.aonService.getCompanies().subscribe(
      (r: Company[]) => {

        this.companies = this.companies ? this.companies.concat(r.filter(f => this.companyFilter(f))) : r.filter(f => this.companyFilter(f));
        this.companies.sort(function (a, b) {
          if (a.name.toUpperCase() > b.name.toUpperCase()) {
            return 1;
          }
          if (a.name.toUpperCase() < b.name.toUpperCase()) {
            return -1;
          }
          // a must be equal to b
          return 0;
        });
        if(!this.aonService.end){
          this.init();
        }
      },
      (error: any) => this.closeSession()
    );
  }

  closeSession(): void {
    localStorage.clear();
    this.service.isUserLoggedIn = false;
    this.aonService.close();
    this.service.close();
    this.router.navigate(['login']);
  }

  ngOnDestroy() {

  }

  onSelect(company: Company): void {
    this.service.company = company;
    localStorage.setItem('aon_domain_id', `${company.id}`);
    localStorage.setItem('aon_domain_name', company.domain);
    this.aonService.company = company;
    RootLoader.rootPanel('<aon-desktop></aon-desktop>', '#f1f1f1');
  }

  public onScroll() {

  }
}
