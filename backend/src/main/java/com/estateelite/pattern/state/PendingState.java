package com.estateelite.pattern.state;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;

public class PendingState implements AppointmentState {

    @Override
    public void handle(Appointment appointment, AppointmentStatus nextStatus) {
        switch (nextStatus) {
            case CONFIRMED, DECLINED, CANCELLED -> appointment.setStatus(nextStatus);
            case PENDING -> throw new BadRequestException("Appointment is already PENDING");
            case COMPLETED -> throw new BadRequestException(
                    "Cannot complete a PENDING appointment – confirm it first");
            default -> throw new BadRequestException("Unsupported transition from PENDING to " + nextStatus);
        }
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.PENDING;
    }
}
