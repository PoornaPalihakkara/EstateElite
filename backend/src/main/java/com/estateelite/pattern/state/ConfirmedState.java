package com.estateelite.pattern.state;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;

public class ConfirmedState implements AppointmentState {

    @Override
    public void handle(Appointment appointment, AppointmentStatus nextStatus) {
        switch (nextStatus) {
            case COMPLETED, CANCELLED -> appointment.setStatus(nextStatus);
            case CONFIRMED -> throw new BadRequestException("Appointment is already CONFIRMED");
            case PENDING, DECLINED -> throw new BadRequestException(
                    "Cannot move a CONFIRMED appointment to " + nextStatus);
            default -> throw new BadRequestException("Unsupported transition from CONFIRMED to " + nextStatus);
        }
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.CONFIRMED;
    }
}
