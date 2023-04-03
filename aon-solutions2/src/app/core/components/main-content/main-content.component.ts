import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { SidenavService } from '../sidenav/sidenav.service';

@Component({
  selector: 'app-main-content',
  templateUrl: './main-content.component.html',
  styleUrls: ['./main-content.component.scss']
})
export class MainContentComponent implements OnInit {

  selectedProduct: any;

  constructor(public service : SidenavService) { }

  ngOnInit(): void {
  }
  
  onSelectedProduct(selected:any) {
    this.selectedProduct = selected;
  }
}
