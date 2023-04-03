import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';

export interface ShortcutDashboard {
  shape : string;
  name : string;
}

export interface MenuItems {
  shape: string;
  name: string;
  color: string;
}

export interface ChartItem {
  shape: string;
  name: string;
  chart: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(private breakpointObserver: BreakpointObserver) {}

  shortcuts : ShortcutDashboard[] = [
    {shape: 'receipt',name:'MIS ÚLTIMAS FACTURAS'},
    {shape: 'add_box',name:'CREAR FACTURA'},
    {shape: 'person_add',name:'DAR DE ALTA EMPLEADO'},
    {shape: 'add_comment',name:'CREAR CONSULTA'},
    {shape: 'add_shopping_cart',name:'CONTRATAR SERVICIOS'},
    {shape: 'alarm',name:'CONTROL HORARIO'},
    {shape: 'group_add',name:'CREAR USUARIO'}
  ];

  menuItems : MenuItems[] = [
    {shape: 'euro_symbol',name:'Panel de Impuestos',color:'#fb982e'},
    {shape: 'people',name:'Panel de empleados',color:'#33a9a9'},
    {shape: 'assessment',name:'Facturación',color:'#4f91ff'},
    {shape: 'description',name:'Documentación',color:'#ef6292'}
  ]

  chartItems : ChartItem[] = [
    {shape: 'show_chart',name:'Ventas/Gastos',chart:''},
    {shape: 'bar_chart',name:'Cobros/Pagos',chart:''},
  ]

  cards = this.breakpointObserver.observe(Breakpoints.Tablet).pipe(
    map(({ matches }) => {
      if (matches) {
        return [
        // { title: 'Card 1', cols: 12, rows: 5, content: "" },
        // { title: 'Card 2', cols: 12, rows: 5, content: "" },
        // { title: 'Card 3', cols: 12, rows: 5, content: "" },
        // { title: 'Card 4', cols: 12, rows: 5, content: "" },
        { title: 'Bandeja', cols: 24, rows: 6, content: "" },
        ];
      }

      return [
        // { title: 'Card 1', cols: 7, rows: 5, content: "" },
        // { title: 'Card 2', cols: 7, rows: 5, content: "" },
        // { title: 'Card 3', cols: 6, rows: 5, content: "" },
        // { title: 'Card 4', cols: 4, rows: 5, content: "" },
        { title: 'Bandeja', cols: 24, rows: 6, content: "" },
      ];
    })
  );

  ngOnInit(): void {
    
  }

}
