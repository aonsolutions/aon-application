import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-list-dashboard',
  templateUrl: './list-dashboard.component.html',
  styleUrls: ['./list-dashboard.component.scss']
})
export class ListDashboardComponent implements OnInit {
  @Input() type: number = 0;
  @Input() name: string = "";
  @Input() description: string = "";
  @Input() title: string = "";
  @Input() date: any = "";
  @Input() status: string = "";
  public icon: string = "";

  ngOnInit(): void {
    this.setIcon();
  }

  private setIcon(): void {
    switch (this.status) {
      case 'pendiente':
        this.icon = "playlist_add_check";
        break;
      case 'nueva':
        this.icon = "notifications";
        break;
      case 'abierta':
        this.icon = "message";
        break;
      default:
        this.icon = "";
        break;
    }
  }
}
