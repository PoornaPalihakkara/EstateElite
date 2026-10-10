package com.estateelite.pattern.state;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;

public class DeclinedState implements AppointmentState {

    @Override
    public void handle(Appointment appointment, AppointmentStatus nextStatus) {
        throw new BadRequestException(
                "Appointment has been DECLINED – no further status changes are allowed (requested: " + nextStatus + ")");
    }

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.DECLINED;
    }
}
