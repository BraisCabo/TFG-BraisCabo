import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentExamPageComponent } from './student-exam-page.component';

describe('StudentExamPageComponent', () => {
  let component: StudentExamPageComponent;
  let fixture: ComponentFixture<StudentExamPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentExamPageComponent]
    });
    fixture = TestBed.createComponent(StudentExamPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
