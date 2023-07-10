import { Component, Input, OnInit } from '@angular/core';
import { BankService } from '../../../../core/services/bank.service';
import { Bank } from 'src/app/core/models/class/bank';
import { Observable } from 'rxjs';

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
  banks: Bank[] = [];
  totalAmount: number = 0;
  @Input() public bankList: Observable<Bank[]> | undefined;

  constructor() {}

  ngOnInit(): void {
    if (this.bankList) {
      this.bankList.subscribe((banks) => {
        this.banks = banks
        this.calculateTotalAmount()


        });
    }
  }
  calculateTotalAmount(): void {
    this.totalAmount = this.banks.reduce((sum, bank) => sum + bank.Total, 0);
  }

  hasBanks(): boolean {
    return this.banks.length > 0;
  }

}
