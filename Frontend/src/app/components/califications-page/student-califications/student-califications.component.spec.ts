import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentCalificationsComponent } from './student-califications.component';

describe('StudentCalificationsComponent', () => {
  let component: StudentCalificationsComponent;
  let fixture: ComponentFixture<StudentCalificationsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentCalificationsComponent]
    });
    fixture = TestBed.createComponent(StudentCalificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
