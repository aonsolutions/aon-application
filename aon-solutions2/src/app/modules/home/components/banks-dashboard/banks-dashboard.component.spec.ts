import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BanksDashboardComponent } from './banks-dashboard.component';
//import { Bank } from 'src/app/core/models/class/bank';
import { Observable, of } from 'rxjs';

describe('BanksDashboardComponent', () => {
  let component: BanksDashboardComponent;
  let fixture: ComponentFixture<BanksDashboardComponent>;
  //let bankList: Observable<Bank[]> ;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        BanksDashboardComponent
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BanksDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set the models when bankList is available', () => {
  //  const bankList: Bank[] = [
  //    new Bank('Caixa Bank', 1500, 'mypathtofolder3'),
  //    new Bank('Banco Nación', 500, 'mypathtofolder3'),
  //    new Bank('Bankinter', 2500, 'mypathtofolder3')
  //];
   // const banks: Observable<Bank[]> = of(bankList);
    spyOn(component, 'calculateTotalAmount');
   // component.bankList = banks;
    component.ngOnInit();

   // expect(component.banks).toEqual(bankList); 
    expect(component.calculateTotalAmount).toHaveBeenCalled(); 
});

  it('should set models as empty array when bankList is undefined', () => {
    component.ngOnInit();

  //  expect(component.banks).toEqual([]);
});

  it('should calculate the total amount correctly', () => {
//    const bankList: Bank[] = [
//      new Bank('Caixa Bank', 1500, 'mypathtofolder3'),
//      new Bank('Banco Nación', 500, 'mypathtofolder3')
//    ];
//    component.banks = bankList; 
    component.calculateTotalAmount();
  
    expect(component.totalAmount).toEqual(2000);
  });
  
});
