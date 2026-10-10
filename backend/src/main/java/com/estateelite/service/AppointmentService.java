package com.estateelite.service;

import com.estateelite.dto.request.AppointmentRequest;
import com.estateelite.dto.request.AppointmentStatusUpdateRequest;
import com.estateelite.dto.request.SlotRequest;
import com.estateelite.dto.response.AppointmentResponse;
import com.estateelite.dto.response.SlotResponse;
import com.estateelite.exception.AccessDeniedCustomException;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.*;
import com.estateelite.repository.AppointmentRepository;
import com.estateelite.repository.AvailabilitySlotRepository;
import com.estateelite.repository.PropertyRepository;
import com.estateelite.pattern.state.AppointmentStateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService;

    @Transactional
    public SlotResponse createSlot(SlotRequest request, User agent) {
        Property property = null;
        if (request.getPropertyId() != null) {
            property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        }
        AvailabilitySlot slot = AvailabilitySlot.builder()
                .agent(agent)
                .property(property)
                .slotDateTime(request.getSlotDateTime())
                .isBooked(false)
                .build();
        availabilitySlotRepository.save(slot);
        return SlotResponse.from(slot);
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getOpenSlots(Long agentId) {
        return availabilitySlotRepository.findByAgentIdAndIsBookedFalse(agentId)
                .stream().map(SlotResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public SlotResponse updateSlot(Long id, SlotRequest request, User agent) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));
        if (!slot.getAgent().getId().equals(agent.getId())) {
            throw new AccessDeniedCustomException("You can only update your own slots");
        }
        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new BadRequestException("Cannot update a slot that is already booked");
        }
        if (request.getSlotDateTime() != null) {
            slot.setSlotDateTime(request.getSlotDateTime());
        }
        if (request.getPropertyId() != null) {
            Property property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            slot.setProperty(property);
        }
        availabilitySlotRepository.save(slot);
        return SlotResponse.from(slot);
    }

    @Transactional
    public void deleteSlot(Long id, User agent) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));
        if (!slot.getAgent().getId().equals(agent.getId())) {
            throw new AccessDeniedCustomException("You can only delete your own slots");
        }
        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new BadRequestException("Cannot delete a slot that is already booked – cancel the appointment instead");
        }
        availabilitySlotRepository.delete(slot);
    }

    @Transactional
    public AppointmentResponse requestAppointment(AppointmentRequest request, User buyer) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new BadRequestException("This slot has already been booked");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        Appointment appointment = Appointment.builder()
                .property(property)
                .agent(slot.getAgent())
                .buyer(buyer)
                .slotDateTime(slot.getSlotDateTime())
                .status(AppointmentStatus.PENDING)
                .build();
        appointmentRepository.save(appointment);

        slot.setIsBooked(true);
        availabilitySlotRepository.save(slot);

        // Notify the agent who owns the slot
        notificationService.notify(slot.getAgent(),
                buyer.getFullName() + " requested a viewing for \"" + property.getTitle() + "\"",
                NotificationType.APPOINTMENT_REQUESTED);

        // Notify the property seller/owner so they know a viewing was booked on their listing
        User seller = property.getSeller();
        if (seller != null && (slot.getAgent() == null || !seller.getId().equals(slot.getAgent().getId()))) {
            notificationService.notify(seller,
                    buyer.getFullName() + " requested a viewing for your property \"" + property.getTitle()
                            + "\" on " + slot.getSlotDateTime()
                            + ". Agent: " + slot.getAgent().getFullName() + ".",
                    NotificationType.APPOINTMENT_REQUESTED);
        }

        return AppointmentResponse.from(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByAgent(Long agentId) {
        return appointmentRepository.findByAgentId(agentId).stream().map(AppointmentResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByBuyer(Long buyerId) {
        return appointmentRepository.findByBuyerId(buyerId).stream().map(AppointmentResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAll() {
        return appointmentRepository.findAll().stream().map(AppointmentResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatusUpdateRequest request, User actingUser) {
        Appointment appointment = findOrThrow(id);
        assertParticipant(appointment, actingUser);

        if (request.getNewSlotDateTime() != null) {
            appointment.setSlotDateTime(request.getNewSlotDateTime());
        }

        // Double-booking check only when moving to CONFIRMED
        if (request.getStatus() == AppointmentStatus.CONFIRMED) {
            boolean conflict = !appointmentRepository
                    .findByAgentIdAndSlotDateTimeAndStatus(appointment.getAgent().getId(),
                            appointment.getSlotDateTime(), AppointmentStatus.CONFIRMED)
                    .stream().filter(a -> !a.getId().equals(appointment.getId())).toList().isEmpty();
            if (conflict) {
                throw new BadRequestException("Double-booking prevented: agent already has a confirmed appointment at this time");
            }
        }

        // STATE PATTERN: legal transitions are enforced by AppointmentStateContext
        // (Member 3). Illegal moves (e.g. PENDING → COMPLETED) throw BadRequestException.
        AppointmentStateContext stateContext = new AppointmentStateContext(appointment.getStatus());
        stateContext.transition(appointment, request.getStatus());

        appointmentRepository.save(appointment);

        NotificationType type = switch (request.getStatus()) {
            case CONFIRMED -> NotificationType.APPOINTMENT_CONFIRMED;
            case DECLINED -> NotificationType.APPOINTMENT_DECLINED;
            case CANCELLED -> NotificationType.APPOINTMENT_CANCELLED;
            default -> NotificationType.GENERAL;
        };
        notificationService.notify(appointment.getBuyer(),
                "Your viewing for \"" + appointment.getProperty().getTitle() + "\" is now " + request.getStatus(),
                type);

        // Keep the property seller informed of status changes on their listing
        User seller = appointment.getProperty().getSeller();
        if (seller != null
                && !seller.getId().equals(appointment.getBuyer().getId())
                && (appointment.getAgent() == null || !seller.getId().equals(appointment.getAgent().getId()))) {
            notificationService.notify(seller,
                    "Viewing for your property \"" + appointment.getProperty().getTitle()
                            + "\" (buyer: " + appointment.getBuyer().getFullName()
                            + ") is now " + request.getStatus() + ".",
                    type);
        }

        return AppointmentResponse.from(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id, User actingUser) {
        Appointment appointment = findOrThrow(id);
        assertParticipant(appointment, actingUser);
        appointmentRepository.delete(appointment);
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private void assertParticipant(Appointment appointment, User user) {
        boolean isAgent = appointment.getAgent().getId().equals(user.getId());
        boolean isBuyer = appointment.getBuyer().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isAgent && !isBuyer && !isAdmin) {
            throw new AccessDeniedCustomException("You are not a participant in this appointment");
        }
    }
}
