import { Component, OnInit } from '@angular/core';
import { BankService } from '../../../../core/services/bank.service';

export interface Banks {
  name: string;
  amount: string;
}

@Component({
  selector: 'app-banks-dashboard',
  templateUrl: './banks-dashboard.component.html',
  styleUrls: ['./banks-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class BanksDashboardComponent implements OnInit {


  totalAmount: string = '29.987,76 €';

  banks : Banks[] = [
    {name: 'CaixaBank', amount: '5.487,23'},
    {name: 'Bankinter', amount: '8.457,43'},
    {name: 'BBVA', amount: '15.345,12'},
  ]

  constructor(
     public bankService :BankService
   ) {
     bankService.getBankList().then(
       (response) => {console.log(response)}
     )
   }


  ngOnInit(): void {
  }

}


