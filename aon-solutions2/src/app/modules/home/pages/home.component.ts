import { Component, OnInit } from '@angular/core';
import { CollectionFactory, IBank, ICollection } from 'libraries/AonSDK/src/aon';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';
import { MessageService } from 'src/app/core/services/message.service';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { CountryService } from 'src/app/core/services/country.service';

 interface ShortcutDashboard {
  shape : string;
  name  : string;
}

interface ChartItem {
  name        : string;
  typeDate    : number;
}

 interface MenuItems {
  routerlink: string;
  shape     : string;
  name      : string;
  class     : string;
}

@Component({
  selector    : 'app-home',
  templateUrl : './home.component.html',
  styleUrls   : ['./home.component.scss']
})

export class HomeComponent implements OnInit {
  shortcuts   : ShortcutDashboard[] = [];
  chartItems  : ChartItem[]         = [];
  menuItems   : MenuItems[]         = [];

  public collectionFactory  = new CollectionFactory();

  //banks area
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );
  public banks$ = this.banksSubject.asObservable();

  constructor(
    private bankService     : BankService,
    private taxModelService : TaxModelService,
    private reportingService: ReportingService,
    private messageService  : MessageService,
    private translateService: TranslateService,
  ) {
    this.translateService.get([
      'HOME.SALES_EXPENSES', 'HOME.COLLECTIONS_PAYMENTS', 'HOME.CREATE_INVOICE',
      'HOME.REGISTER_EMPLOYEE', 'HOME.CREATE_QUERY', 'HOME.TIMING', 'HOME.ASSESSMENT',
      'HOME.TAX_PANEL', 'HOME.EMPLOYEE_PANEL', 'HOME.DOCUMENTATION',
    ]).subscribe((result) => {
      this.shortcuts = [
        { shape: 'add_box'    , name: result['HOME.CREATE_INVOICE'] },
        { shape: 'person_add' , name: result['HOME.REGISTER_EMPLOYEE'] },
        { shape: 'add_comment', name: result['HOME.CREATE_QUERY'] },
        { shape: 'alarm'      , name: result['HOME.TIMING'] },
      ];
      this.chartItems = [
        {
          name      : result['HOME.SALES_EXPENSES'],
          typeDate  : 1, // Ventas/Gastos
        },
        {
          name      : result['HOME.COLLECTIONS_PAYMENTS'],
          typeDate  : 2, // Cobros/Pagos
        },
      ];
      this.menuItems = [
        {
          routerlink: '/billing',
          shape     : 'assessment',
          name      : result['HOME.ASSESSMENT'],
          class     : 'button-dashboard blue'
        },
        {
          routerlink: '/tax-panel',
          shape     : 'euro_symbol',
          name      : result['HOME.TAX_PANEL'],
          class     : 'button-dashboard orange'
        },
        {
          routerlink: '/employee-panel',
          shape     : 'people',
          name      : result['HOME.EMPLOYEE_PANEL'],
          class     : 'button-dashboard green'
        },
        {
          routerlink: '/documentation',
          shape     : 'description',
          name      : result['HOME.DOCUMENTATION'],
          class     : 'button-dashboard pink'
        },
      ];
      //bankService
      this.bankService.getBankList().then((response) => {
        this.banks = response;
        this.banksSubject.next(this.banks);
      });

    });
  }

  ngOnInit(): void { }
}
