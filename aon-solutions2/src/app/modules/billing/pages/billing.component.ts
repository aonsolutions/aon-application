import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, IBank, ICollection } from 'libraries/AonSDK/aon';
import { BehaviorSubject } from 'rxjs';
import { BankService } from 'src/app/core/services/bank.service';

interface ChartItem {
  name: string;
  typeDate: number;
  direction: string;
}

@Component({
  selector: 'app-billing',
  templateUrl: './billing.component.html',
  styleUrls: ['./billing.component.scss'],
})
export class BillingComponent implements OnInit {
  chartItems: ChartItem[] = [];

  public collectionFactory = new CollectionFactory();

  //banks area
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );

  public banks$ = this.banksSubject.asObservable();

  constructor(
    private translateService: TranslateService,
    private bankService: BankService
  ) {
    this.translateService
      .get(['BILLING.SALES', 'BILLING.BILLS', 'BILLING.REPORTS'])
      .subscribe((result) => {

        this.chartItems = [
          {
            name: result['BILLING.SALES'],
            typeDate: 1,
            direction: '1',
          },
          {
            name: result['BILLING.BILLS'],
            typeDate: 2,
            direction: '2',
          },
          {
            name: result['BILLING.REPORTS'],
            typeDate: 3,
            direction: '3',
          },
        ];

      });

    //bankService
    this.bankService.getBankList().then((response) => {
      this.banks = response;
      this.banksSubject.next(this.banks);
    });
  }

  ngOnInit(): void {}
}
