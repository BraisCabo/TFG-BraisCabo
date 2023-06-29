import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherCalificationsComponent } from './teacher-califications.component';

describe('TeacherCalificationsComponent', () => {
  let component: TeacherCalificationsComponent;
  let fixture: ComponentFixture<TeacherCalificationsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeacherCalificationsComponent]
    });
    fixture = TestBed.createComponent(TeacherCalificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
