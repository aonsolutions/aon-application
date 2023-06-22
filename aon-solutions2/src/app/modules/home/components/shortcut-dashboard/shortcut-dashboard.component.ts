import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-shortcut-dashboard',
  templateUrl: './shortcut-dashboard.component.html',
  styleUrls: ['./shortcut-dashboard.component.scss'],
  host : {
    '[style.height]' : "'100%'",
    // '[style.margin-right]' : "'15px'"
  }
})
export class ShortcutDashboardComponent implements OnInit {

  @Input() shape = '';
  @Input() name = '';

  constructor() { }

  ngOnInit(): void {
  }

}
