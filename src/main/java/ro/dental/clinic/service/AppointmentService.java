package ro.dental.clinic.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.domain.AppointmentRepository;
import ro.dental.clinic.mapper.AppointmentMapper;
import ro.dental.clinic.model.AppointmentDetailsList;
import ro.dental.clinic.model.AppointmentDetailsListItem;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;

    private static final BiPredicate<AppointmentEty, AppointmentFilter> filter = (appointment, filters) ->
            (filters.getStatus() == null || filters.getStatus().equals(appointment.getStatus().name()))
                    &&
                    (filters.getSearch() == null || (
                            appointment.getDoctor().getUser().getFirstName().toUpperCase()
                                    .contains(filters.getSearch().toUpperCase())
                                    || appointment.getDoctor().getUser().getLastName().toUpperCase()
                                    .contains(filters.getSearch().toUpperCase())))
                    &&
                    (filters.getUserId() == null || (
                            appointment.getDoctor().getUser().getUserId().contains(filters.getUserId())
                    ) || appointment.getPatient().getUser().getUserId().contains(filters.getUserId()));

    @AllArgsConstructor
    @Getter
    @Setter
    private static class AppointmentFilter {

        private String status;
        private String search;
        private String userId;

    }

    @Transactional
    public AppointmentDetailsList getAppointmentsDetails(String status, String search) {
        var appointmentDetailsList = new AppointmentDetailsList();
        var appointmentStream = appointmentRepository.findAll().stream();
        String userId = securityAccessTokenProvider.getUserIdFromAuthToken();
        AppointmentFilter appointmentFilter = new AppointmentFilter(status, search, userId);

        Comparator<AppointmentDetailsListItem> compareByStatus =
                Comparator.comparing(
                        appointmentDetailsListItem -> appointmentDetailsListItem.getStatus().getPriority());
        Comparator<AppointmentDetailsListItem> compareByCrtTms = Comparator.comparing(
                AppointmentDetailsListItem::getCrtTms);

        appointmentDetailsList.setItems(
                appointmentStream
                        .filter(leaveRequestEty -> filter.test(leaveRequestEty, appointmentFilter))
                        .map(AppointmentMapper.INSTANCE::mapLeaveRequestEtyToLeaveRequestDto)
                        .sorted(compareByStatus.thenComparing(compareByCrtTms))
                        .collect(Collectors.toList()));

        return appointmentDetailsList;
    }

    @Transactional
    public List<String> getFreeTimeSlotForADay(String date) {
        var appointments = appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getDate().equals(date)).collect(Collectors.toList());
        List<String> freeTimeSlot = new ArrayList<>();
        freeTimeSlot.add("09:00");
        freeTimeSlot.add("10:00");
        freeTimeSlot.add("11:00");
        freeTimeSlot.add("12:00");
        freeTimeSlot.add("13:00");
        freeTimeSlot.add("14:00");
        freeTimeSlot.add("15:00");
        freeTimeSlot.add("16:00");
        return freeTimeSlot;
    }

}
