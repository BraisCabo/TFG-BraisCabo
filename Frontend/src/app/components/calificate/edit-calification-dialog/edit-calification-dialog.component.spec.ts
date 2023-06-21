import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCalificationDialogComponent } from './edit-calification-dialog.component';

describe('EditCalificationDialogComponent', () => {
  let component: EditCalificationDialogComponent;
  let fixture: ComponentFixture<EditCalificationDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditCalificationDialogComponent]
    });
    fixture = TestBed.createComponent(EditCalificationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
