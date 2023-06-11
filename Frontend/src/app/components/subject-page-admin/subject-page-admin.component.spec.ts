import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubjectPageAdminComponent } from './subject-page-admin.component';

describe('SubjectPageAdminComponent', () => {
  let component: SubjectPageAdminComponent;
  let fixture: ComponentFixture<SubjectPageAdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SubjectPageAdminComponent]
    });
    fixture = TestBed.createComponent(SubjectPageAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
