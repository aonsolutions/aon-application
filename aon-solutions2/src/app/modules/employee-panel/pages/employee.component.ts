import { marks } from './../../../../../libraries/AonSDK/src/models/Mark';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ContractService } from 'src/app/core/services/contract.service';
import {
  CollectionFactory,
  Factory,
  ICollection,
  IContract,
  IEmployee,
  IMark,
} from 'libraries/AonSDK/src/aon';
import { DateFormat } from 'src/app/core/utilities/time';
import { EmployeeService } from 'src/app/core/services/employee.service';
import { MarkService } from 'src/app/core/services/mark.service';

export interface Tabs {
  name: string; // Nombre de la pestaña
}

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss'],
})
export class EmployeeComponent implements OnInit {
  tabIndex: number = 0;
  spinner: boolean = false;
  entityFactory = new Factory();
  contracts: ICollection<IContract> =
    new CollectionFactory().createContractCollection();
  displayedColumns: string[] = [
    'employee',
    'grossCost',
    'type',
    'date',
    'workCenter',
    'marcaje',
  ];
  headerTable: any = {};

  tabs: Tabs[] = [{ name: 'tab1' }, { name: 'tab2' }];
  buttonsGastos: any[] = [];
  showDetailCurrentContract: boolean = false;
  showDetailExpiredContract: boolean = false;
  search: string = '';
  showSendButton: boolean = false;
  showSendButtons: boolean = false;
  contratosActivos: IContract[] = [];
  contratosInactivos: IContract[] = [];
  empleadoDetail: IEmployee | undefined;
  marcajeEmpleado: IMark | undefined;
  selectedContract: IContract | undefined;

  bodyTable: any = [];

  constructor(
    private contractService: ContractService,
    private employeeService: EmployeeService,
    private markService: MarkService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.initializeComponent();
  }

  private initializeComponent() {
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

        this.tabs = [
          { name: result['EMPLOYEE_PANEL.CURRENT_CONTRACTS'] },
          { name: result['EMPLOYEE_PANEL.EXPIRED_CONTRACTS'] },
        ];

        this.headerTable = {
          employee: result['EMPLOYEE_PANEL.EMPLOYEE'],
          grossCost: result['EMPLOYEE_PANEL.GROSSCOST'],
          type: result['EMPLOYEE_PANEL.TYPE_OF_CONTRACT'],
          date: result['EMPLOYEE_PANEL.DATE_START_END'],
          workCenter: result['EMPLOYEE_PANEL.WORKCENTER'],
          marcaje: result['HOME.TIMING'],
        };

        this.updateTableData();
      });
  }

  addEmployee() {
    // Agregar la lógica para agregar un empleado
  }

  private updateTableData() {
    this.contractService.getContractList().then((response) => {
      this.spinner = false;
      this.contratosActivos = [];
      this.contratosInactivos = [];

      response.forEach((contract, contractKey) => {
        const column: any = {
          key: contractKey,
          employee: `${contract.Name} ${contract.LastName}`,
          grossCost: `${contract.GrossCost} &euro;`,
          type: contract.Type,
        };

        if (contract.StartDate && contract.EndDate) {
          const formattedStartDate = DateFormat(
            contract.StartDate,
            'dd/MM/yyyy'
          );
          const formattedEndDate = DateFormat(contract.EndDate, 'dd/MM/yyyy');
          column.date = `${formattedStartDate} - ${formattedEndDate}`;
        } else {
          column.date = '';
        }

        column.workCenter = contract.WorkCenter;
        column.marcaje = '';

        if (contract.Active) {
          this.contratosActivos.push(column);
        } else {
          this.contratosInactivos.push(column);
        }
      });

      this.bodyTable =
        this.tabIndex === 0 ? this.contratosActivos : this.contratosInactivos;
      console.log('esto es bodytable', this.bodyTable);
    });
  }

  changeTabIndex(index: number) {
    this.tabIndex = index;
    this.closeDetailCurrentContract();
    this.closeDetailExpiredContract();
    this.updateTableData();
  }

  searchContract(search: string) {
    if (!search) {
      // Si el término de búsqueda está vacío, mostrar todos los elementos de la tabla
      this.updateTableData();
      return;
    }

    // Convertir el término de búsqueda a minúsculas y quitar las tildes
    search = search.toLowerCase();
    search = this.removeAccents(search);

    // Filtrar los elementos en función del término de búsqueda
    this.bodyTable = this.bodyTable.filter((item: any) =>
      Object.values(item).some((value: any) => {
        // Convierte el valor a minúsculas y quita las tildes antes de comparar
        const cleanedValue = this.removeAccents(value.toString().toLowerCase());
        return cleanedValue.includes(search);
      })
    );

    // Filtra en la columna "employee".
    //   this.bodyTable = this.bodyTable.filter((item) => {
    //     return item.employee.toLowerCase().includes(search);
    //   });
    // }
  }

  // Función para quitar las tildes
  removeAccents(str: string) {
    return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  async rowClick(contract: any) {

    this.selectedContract = await this.contractService.getContract(contract.key)
        // Obtener empleado
    this.empleadoDetail = await this.employeeService.getEmployee(contract.key);
    this.marcajeEmpleado = await this.markService.getMark(contract.key);
    console.log('marcaje', this.marcajeEmpleado);

    const isSameRow = this.bodyTable && this.bodyTable.Id === contract.key;

    if (this.tabIndex === 0) {
      this.showDetailCurrentContract = !isSameRow
        ? true
        : !this.showDetailCurrentContract;
    } else {
      this.showDetailExpiredContract = !isSameRow
        ? true
        : !this.showDetailExpiredContract;
    }
  }


  // Cerrar details contratos vigentes
  closeDetailCurrentContract() {
    this.showDetailCurrentContract = false;
  }

  // Cerrar details contratos extinguidos
  closeDetailExpiredContract() {
    this.showDetailExpiredContract = false;
  }
}
