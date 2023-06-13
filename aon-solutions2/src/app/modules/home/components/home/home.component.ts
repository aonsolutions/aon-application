import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit, ViewChild } from '@angular/core';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  // constructor(public invoiceService: InvoiceService) {
  //   this.invoiceService.getInvoices().subscribe((response)=>{
  //     console.log(response)
  //   })
  // }


  constructor(breakpointObserver: BreakpointObserver){

  }

  ngOnInit(): void {
    
  }

  
  
}
