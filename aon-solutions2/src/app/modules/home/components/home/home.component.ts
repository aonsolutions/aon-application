import { Component, OnInit } from '@angular/core';
import { Tabs } from 'src/app/core/models/interface/tabs';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  tabIndex: number = 0;

  tabs: Tabs[] = [
    { name: 'tab1', icon: 'create', color: 'black' },
    { name: 'tab2', icon: 'delete', color: 'black' },
    { name: 'tab3', color: 'black' },
  ];

  constructor() {
  }

  ngOnInit(): void {
  }
  
}
