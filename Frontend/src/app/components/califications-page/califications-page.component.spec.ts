import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificationsPageComponent } from './califications-page.component';

describe('CalificationsPageComponent', () => {
  let component: CalificationsPageComponent;
  let fixture: ComponentFixture<CalificationsPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CalificationsPageComponent]
    });
    fixture = TestBed.createComponent(CalificationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
