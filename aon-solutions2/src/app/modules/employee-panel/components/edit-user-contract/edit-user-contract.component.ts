import { Component, EventEmitter, OnInit, Output } from '@angular/core';


@Component({
  selector: 'app-edit-user-contract',
  templateUrl: './edit-user-contract.component.html',
  styleUrls: ['./edit-user-contract.component.scss']
})
export class EditUserContractComponent implements OnInit {
  @Output() changeTabIndex: EventEmitter<number> = new EventEmitter<number>();

  constructor() {}

  ngOnInit() {}
  goBack() {
    const tabIndex = 0;
    this.changeTabIndex.emit(tabIndex);
    console.log(tabIndex);
  }
}
