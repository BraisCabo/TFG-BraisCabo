import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificateComponent } from './calificate.component';

describe('CalificateComponent', () => {
  let component: CalificateComponent;
  let fixture: ComponentFixture<CalificateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CalificateComponent]
    });
    fixture = TestBed.createComponent(CalificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
