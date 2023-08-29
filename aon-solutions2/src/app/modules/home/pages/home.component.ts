import { Component, OnInit, ViewChild } from '@angular/core';
import { CollectionFactory, IBank, ICollection, IMessage, ITaxModel } from 'libraries/AonSDK/aon';
import { TranslateService } from '@ngx-translate/core';
import { ChartType } from 'chart.js';
import { MultiDataSet } from 'ng2-charts';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';
import { MessageService } from 'src/app/core/services/message.service';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

export interface ShortcutDashboard {
  shape : string;
  name  : string;
}

interface ChartItem {
  shape       : string;
  name        : string;
  chartLabels : string[];
  chartData   : MultiDataSet;
  chartType   : ChartType;
  colors      : string[];
}

export interface MenuItems {
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
  private chartItemsSubject = new BehaviorSubject<any[]>([]);
  public chartItems$        = this.chartItemsSubject.asObservable();
  public collectionFactory  = new CollectionFactory();

  //banks area
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );
  public banks$ = this.banksSubject.asObservable();

  //modelTax area
  models: ICollection<ITaxModel> = this.collectionFactory.createTaxModelCollection();
  private modelsSubject = new BehaviorSubject<ICollection<ITaxModel>>(
    this.collectionFactory.createTaxModelCollection()
  );
  public models$ = this.modelsSubject.asObservable();

  //Inbox area
  messages: ICollection<IMessage> = this.collectionFactory.createMessageCollection();
  private messagesSubject = new BehaviorSubject<ICollection<IMessage>>(
    this.collectionFactory.createMessageCollection()
  );
  public messages$ = this.messagesSubject.asObservable();

  constructor(
    private bankService     : BankService,
    private taxModelService : TaxModelService,
    private reportingService: ReportingService,
    private messageService  : MessageService,
    private translateService: TranslateService
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
          shape: 'show_chart',
          name: result['HOME.SALES_EXPENSES'],
          chartLabels: [],
          chartData: [],
          chartType: 'line',
          colors: [],
        },
        {
          shape: 'bar_chart',
          name: result['HOME.COLLECTIONS_PAYMENTS'],
          chartLabels: [],
          chartData: [],
          chartType: 'bar',
          colors: [],
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

      //taxmodelService
      this.taxModelService.getTaxModelList().then((response) => {
        this.models = response;
        this.modelsSubject.next(this.models);
      });

      //reportingService-VentasGastos
      this.reportingService.getVentasGastos().then((response) => {
        this.chartItems[0].chartType = 'line';
        this.chartItems[0].chartData = response.datasets.map(
          (dataset: any) => dataset.data
        );
        this.chartItems[0].chartLabels = response.label;
        this.chartItemsSubject.next(this.chartItems);
      });

      //reportingService-CobrosPagos
      this.reportingService.getCobrosPagos().then((response) => {
        this.chartItems[1].chartType = 'bar';
        this.chartItems[1].chartData = response.datasets.map(
          (dataset: any) => dataset.data
        );
        this.chartItems[1].chartLabels = response.label;
        this.chartItemsSubject.next(this.chartItems);
      });

      //MessageService
      this.messageService.getMessageList().then((response) => {
        this.messages = response;
        this.messagesSubject.next(this.messages);
      });
    });
  }

  ngOnInit(): void {
  
  }
}
