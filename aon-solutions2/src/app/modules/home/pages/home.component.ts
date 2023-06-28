import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit, ViewChild } from '@angular/core';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

export interface ShortcutDashboard {
  shape : string;
  name : string;
}

export interface ChartItem {
  shape: string;
  name: string;
  chart: string;
}

export interface MenuItems {
  shape: string;
  name: string;
  color: string;
}


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})


export class HomeComponent implements OnInit {


  shortcuts : ShortcutDashboard[] = [
    {shape: 'add_box',name:'CREAR FACTURA'},
    {shape: 'person_add',name:'DAR DE ALTA EMPLEADO'},
    {shape: 'add_comment',name:'CREAR CONSULTA'},
    {shape: 'alarm',name:'MARCAJE'},
   ];

   chartItems : ChartItem[] = [
    {shape: 'show_chart',name:'Ventas/Gastos',chart:''},
    {shape: 'bar_chart',name:'Cobros/Pagos',chart:''},
  ]

  menuItems : MenuItems[] = [
    {shape: 'assessment',name:'Gestión',color:'#4f91ff'},
    {shape: 'euro_symbol',name:'Panel de Impuestos',color:'#fb982e'},
    {shape: 'people',name:'Panel de empleados',color:'#33a9a9'},
    {shape: 'description',name:'Documentación',color:'#ef6292'}
  ]


  constructor(breakpointObserver: BreakpointObserver){

  }

  ngOnInit(): void {

  }



}
