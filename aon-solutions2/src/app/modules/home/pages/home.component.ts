import { Component, OnInit, ViewChild } from '@angular/core';
import { ChartType } from 'chart.js';
import { CollectionFactory, IBank, ICollection, IMessage, ITaxModel } from 'libraries/AonSDK/aon';
import { MultiDataSet } from 'ng2-charts';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';
import { MessageService } from 'src/app/core/services/message.service';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

export interface ShortcutDashboard {
  shape: string;
  name: string;
}

interface ChartItem {
  shape: string;
  name: string;
  chartLabels: string[];
  chartData: MultiDataSet;
  chartType: ChartType;
  colors: string[];
}

export interface MenuItems {
  shape: string;
  name: string;
  color: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  //botones area
  shortcuts: ShortcutDashboard[] = [
    { shape: 'add_box', name: 'CREAR FACTURA' },
    { shape: 'person_add', name: 'DAR DE ALTA EMPLEADO' },
    { shape: 'add_comment', name: 'CREAR CONSULTA' },
    { shape: 'alarm', name: 'MARCAJE' },
  ];

  //chart area
  chartItems: ChartItem[] = [
    {
      shape: 'show_chart',
      name: 'Ventas/Gastos',
      chartLabels:[],
      chartData: [],
      chartType: 'line' ,
      colors: [],
    },
    {
      shape: 'bar_chart',
      name: 'Cobros/Pagos',
      chartLabels: [],
      chartData: [],
      chartType: 'bar',
      colors: [],
    },
  ];
  private chartItemsSubject = new BehaviorSubject<any[]>([]);
  public chartItems$ = this.chartItemsSubject.asObservable();
  public collectionFactory = new CollectionFactory();

  //banks area
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(this.collectionFactory.createBankCollection());
  public banks$ = this.banksSubject.asObservable();

  //modelTax area
  models: ICollection<ITaxModel> = this.collectionFactory.createTaxModelCollection();
  private modelsSubject = new BehaviorSubject<ICollection<ITaxModel>>(this.collectionFactory.createTaxModelCollection());
  public models$ = this.modelsSubject.asObservable();

  //Inbox area
  messages: ICollection<IMessage> = this.collectionFactory.createMessageCollection();
  private messagesSubject = new BehaviorSubject<ICollection<IMessage>>(this.collectionFactory.createMessageCollection());
  public messages$ = this.messagesSubject.asObservable();


  //botones Menu
  menuItems: MenuItems[] = [
    { shape: 'assessment', name: 'Gestión', color: '#4f91ff' },
    { shape: 'euro_symbol', name: 'Panel de Impuestos', color: '#fb982e' },
    { shape: 'people', name: 'Panel de empleados', color: '#33a9a9' },
    { shape: 'description', name: 'Documentación', color: '#ef6292' },
  ];

  constructor(
    public bankService: BankService,
    public taxModelService: TaxModelService,
    public reportingService: ReportingService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
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
      this.chartItems[0].chartData = response.datasets.map((dataset: any) => dataset.data);
      this.chartItems[0].chartLabels = response.label;
      this.chartItemsSubject.next(this.chartItems);
    });


    //reportingService-CobrosPagos
    this.reportingService.getCobrosPagos().then((response) => {
      this.chartItems[1].chartType = 'bar';
      this.chartItems[1].chartData = response.datasets.map((dataset: any) => dataset.data);
      this.chartItems[1].chartLabels = response.label;
      this.chartItemsSubject.next(this.chartItems);
    })

    //MessageService
    this.messageService.getMessageList().then((response) => {
      this.messages = response;
      this.messagesSubject.next(this.messages);
      });




  }
}



