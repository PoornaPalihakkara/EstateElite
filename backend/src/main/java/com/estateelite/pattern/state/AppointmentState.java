package com.estateelite.pattern.state;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;

public interface AppointmentState {

    void handle(Appointment appointment, AppointmentStatus nextStatus);

    AppointmentStatus getStatus();
}
