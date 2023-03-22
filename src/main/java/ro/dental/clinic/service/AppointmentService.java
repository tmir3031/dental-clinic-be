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
import java.util.Comparator;
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
                    (filters.getId_doctor() == null || (
                            appointment.getDoctor().getUser().getUserId().contains(filters.getId_doctor())
                            ));

    @AllArgsConstructor
    @Getter
    @Setter
    private static class AppointmentFilter {

        private String status;
        private String search;
        private String id_doctor;
    }

    @Transactional
    public AppointmentDetailsList getAppointmentsDetails(String status, String search) {
        var appointmentDetailsList = new AppointmentDetailsList();
        var appointmentStream = appointmentRepository.findAll().stream();
        AppointmentFilter appointmentFilter = new AppointmentFilter(status, search, securityAccessTokenProvider.getUserIdFromAuthToken());

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

}
