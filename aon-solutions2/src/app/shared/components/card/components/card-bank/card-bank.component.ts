import { Component, OnInit } from '@angular/core';
import { ErrorResponse, IBank, ICollection } from 'libraries/AonSDK/src/aon';
import { BankService } from 'src/app//core/services/bank.service';

@Component({
  selector    : 'app-card-bank',
  templateUrl : './card-bank.component.html',
  styleUrls   : ['./card-bank.component.scss']
})

export class CardBankComponent implements OnInit {
  spinner     : boolean = true;
  totalAmount : number  = 0;
  banks      !: ICollection<IBank>;

  constructor(
    private bankService: BankService
  ) {
    this.bankService.getBankList().then((response) => {
      this.banks = response;
      response.forEach((bank) => {
        if(typeof bank.Total == 'number')
        this.totalAmount += bank.Total;
      });
      this.spinner = false;
    });
  }

  ngOnInit(): void {

  }

  hasBanks(): boolean {
    if (!this.banks) {
      return false;
    } else {
      return this.banks.size() > 0;
    }
  }

}
