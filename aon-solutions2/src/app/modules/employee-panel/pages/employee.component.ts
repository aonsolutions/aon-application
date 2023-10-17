import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ContractService } from 'src/app/core/services/contract.service';

export interface Tabs {
  name: string; // Nombre de la tab
}
@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss'],
})
export class EmployeeComponent implements OnInit {
  tabIndex: number = 0;
  spinner: boolean = false;

  displayedColumns: string[] = [
    'employee',
    'grossCost',
    'type',
    'date',
    'workCenter',
    'marcaje',
  ];
  headerTable: any = {};
  contracts: any;
  tabs: Tabs[] = [{ name: 'tab1' }, { name: 'tab2' }];
  buttonsGastos: any[] = [];
  showDetail: boolean = false;


  //hardcodeo para mostrar showdetail
  dataBody: any[] = [
    {
      key: 1,
      employee: 'Antonia Diaz',
      grossCost: '1500',
      type: 'Indefinido',
      date: '30/05/2023 - 01/06/2023',
      workCenter: 'Principal',
      marcaje: '20 horas',
    },
    {
      key: 2,
      employee: 'Antonia Diaz',
      grossCost: '1500',
      type: 'Indefinido',
      date: '30/05/2023 - 01/06/2023',
      workCenter: 'Principal',
      marcaje: '20 horas',
    },
    {
      key: 3,
      employee: 'Antonia Diaz',
      grossCost: '1500',
      type: 'Indefinido',
      date: '30/05/2023 - 01/06/2023',
      workCenter: 'Principal',
      marcaje: '20 horas',
    },
  ];

  bodyTable: any = [];

  constructor(
    public contractService: ContractService,
    private translateService: TranslateService
  ) {
    this.translateService
      .get([
        'EMPLOYEE_PANEL.EMPLOYEE',
        'EMPLOYEE_PANEL.GROSSCOST',
        'EMPLOYEE_PANEL.TYPE_OF_CONTRACT',
        'EMPLOYEE_PANEL.DATE_START_END',
        'EMPLOYEE_PANEL.WORKCENTER',
        'EMPLOYEE_PANEL.CURRENT_CONTRACTS',
        'EMPLOYEE_PANEL.EXPIRED_CONTRACTS',
        'HOME.TIMING',
      ])
      .subscribe((result) => {
        this.buttonsGastos = [
          {
            shape: 'picture_as_pdf',
          },
          {
            shape: 'aon_excel',
          },
        ];
        // Cabecera de los tabs
        this.tabs = [
          { name: result['EMPLOYEE_PANEL.CURRENT_CONTRACTS'] },
          { name: result['EMPLOYEE_PANEL.EXPIRED_CONTRACTS'] },
        ];
        // Cabecera de la tabla
        this.headerTable = {
          employee: result['EMPLOYEE_PANEL.EMPLOYEE'],
          grossCost: result['EMPLOYEE_PANEL.GROSSCOST'],
          type: result['EMPLOYEE_PANEL.TYPE_OF_CONTRACT'],
          date: result['EMPLOYEE_PANEL.DATE_START_END'],
          workCenter: result['EMPLOYEE_PANEL.WORKCENTER'],
          marcaje: result['HOME.TIMING'],
        };

        let tableRow: any = [];
        let column: any = {};

        this.dataBody.forEach((contract: any) => {
          console.log(contract);
          column = Object.assign({}, contract);

          column.key = contract.key;
          column.employee = contract.employee;
          column.grossCost = contract.grossCost + ' &euro;';
          column.type = contract.type;
          column.date = contract.date;
          column.workCenter = contract.workCenter;
          column.marcaje = contract.marcaje;

          tableRow.push(column);
        });

        // contractService.getContractList().then((response) => {
        //   this.spinner = false;
        //   // console.log(response);
        //   response.forEach(function (contract, contractKey) {
        //     column = Object.assign({}, contract);
        //     column.key = contractKey;
        //     column.employee = contract.Name;
        //     column.grossCost = contract.GrossCost;
        //     column.type = contract.Type;
        //     column.date = contract.StartDate;
        //     column.workCenter = contract.WorkCenter;
        //     column.marcaje = contract.Name;

        //     tableRow.push(column);
        //   });

        this.bodyTable = tableRow;
        //   // No tenemos modelos en la tabla
        //   if (response.size() === 0) {
        //     this.translateService
        //       .get(['EMPLOYEE_PANEL.NO_CONTRACTS'])
        //       .subscribe((result) => {
        //         this.contracts = result['EMPLOYEE_PANEL.NO_CONTRACTS'];
        //       });
        //   }
        // });
      });
  }

  ngOnInit(): void {}

  addEmployee() {}

  changeTabIndex(index: number) {
    this.tabIndex = index;
    // this.updateTableData();
  }

  private updateTableData() {}
}
