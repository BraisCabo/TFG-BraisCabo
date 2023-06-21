import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetCalificationDialogComponent } from './set-calification-dialog.component';

describe('SetCalificationDialogComponent', () => {
  let component: SetCalificationDialogComponent;
  let fixture: ComponentFixture<SetCalificationDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SetCalificationDialogComponent]
    });
    fixture = TestBed.createComponent(SetCalificationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
