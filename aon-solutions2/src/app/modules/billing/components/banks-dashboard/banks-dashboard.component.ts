import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CollectionFactory, IBank, ICollection } from 'libraries/AonSDK/src/aon';

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
  public collectionFactory = new CollectionFactory();
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  totalAmount: number = 0;
  @Input() public bankList: Observable<ICollection<IBank>> | undefined;

  constructor() {}

  ngOnInit(): void {
    if (this.bankList) {
      this.bankList.subscribe((banks) => {
        this.banks = banks;
        this.calculateTotalAmount();
      });
    }
  }

  calculateTotalAmount(): void {
    this.totalAmount = this.banks.toArray().reduce((sum, bank) => {
      if (!isNaN(bank.Total)) {
        return sum + bank.Total;
      } else {
        return sum;
      }
    }, 0);
  }

  hasBanks(): boolean {
    return this.banks.size() > 0;
  }
}
