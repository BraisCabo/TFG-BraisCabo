import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherExamPageComponent } from './teacher-exam-page.component';

describe('TeacherExamPageComponent', () => {
  let component: TeacherExamPageComponent;
  let fixture: ComponentFixture<TeacherExamPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeacherExamPageComponent]
    });
    fixture = TestBed.createComponent(TeacherExamPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
