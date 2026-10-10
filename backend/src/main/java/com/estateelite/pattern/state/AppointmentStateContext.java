package com.estateelite.pattern.state;

import com.estateelite.exception.BadRequestException;
import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;

import java.util.EnumMap;
import java.util.Map;

public class AppointmentStateContext {

    private static final Map<AppointmentStatus, AppointmentState> STATES = new EnumMap<>(AppointmentStatus.class);

    static {
        STATES.put(AppointmentStatus.PENDING, new PendingState());
        STATES.put(AppointmentStatus.CONFIRMED, new ConfirmedState());
        STATES.put(AppointmentStatus.DECLINED, new DeclinedState());
        STATES.put(AppointmentStatus.CANCELLED, new CancelledState());
        STATES.put(AppointmentStatus.COMPLETED, new CompletedState());
    }

    private AppointmentState currentState;

    public AppointmentStateContext(AppointmentStatus currentStatus) {
        this.currentState = STATES.get(currentStatus);
        if (this.currentState == null) {
            throw new BadRequestException("Unknown appointment status: " + currentStatus);
        }
    }

    public void transition(Appointment appointment, AppointmentStatus nextStatus) {
        currentState.handle(appointment, nextStatus);
        // After a successful transition, move the context to the new state
        // so subsequent calls (if any) see the correct rules.
        this.currentState = STATES.get(appointment.getStatus());
    }

    public AppointmentStatus getCurrentStatus() {
        return currentState.getStatus();
    }
}
