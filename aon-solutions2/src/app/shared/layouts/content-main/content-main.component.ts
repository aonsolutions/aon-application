import { Component, Input, OnInit } from '@angular/core';
import { ContainerService } from '../../services/container.service';

@Component({
  selector: 'app-content-main',
  templateUrl: './content-main.component.html',
  styleUrls: ['./content-main.component.scss']
})
export class ContentMainComponent implements OnInit {

  @Input() heightHeader : number;

  constructor(public containerService : ContainerService) { 
    this.heightHeader = 0;
  }

  ngOnInit(): void {
  }

}
