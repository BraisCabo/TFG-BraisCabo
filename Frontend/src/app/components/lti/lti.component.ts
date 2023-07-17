import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-lti',
  templateUrl: './lti.component.html',
  styleUrls: ['./lti.component.css']
})
export class LtiComponent implements OnInit, OnDestroy {
  private routeSubscription!: Subscription;
  public payload!: string;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.routeSubscription = this.route.queryParams.subscribe(params => {
      this.payload = params['payload'];
      console.log(this.payload);
    });
  }

  ngOnDestroy() {
    this.routeSubscription.unsubscribe();
  }
}
