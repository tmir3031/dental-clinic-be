package ro.dental.clinic.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.domain.AppointmentRepository;
import ro.dental.clinic.domain.DoctorEty;
import ro.dental.clinic.domain.SpecializationRepository;
import ro.dental.clinic.mapper.AppointmentMapper;
import ro.dental.clinic.model.AppointmentDetailsList;
import ro.dental.clinic.model.AppointmentDetailsListItem;
import ro.dental.clinic.model.AppointmentList;
import ro.dental.clinic.model.AppointmentListItem;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;
    private final SpecializationRepository specializationRepository;

    private static final BiPredicate<AppointmentEty, AppointmentDetailsFilter> filterDetails = (appointment, filters) ->
            (filters.getDate() == null || !appointment.getDate()
                    .isBefore(filters.getDate()))
                    &&
                    (filters.getSearchDoctor() == null || (
                            appointment.getDoctor().getUser().getFirstName().toUpperCase()
                                    .contains(filters.getSearchDoctor().toUpperCase())
                                    || appointment.getDoctor().getUser().getLastName().toUpperCase()
                                    .contains(filters.getSearchDoctor().toUpperCase())))
                    &&
                    (filters.getSearchPatient() == null || (
                            appointment.getPatient().getUser().getFirstName().toUpperCase()
                                    .contains(filters.getSearchPatient().toUpperCase())
                                    || appointment.getPatient().getUser().getLastName().toUpperCase()
                                    .contains(filters.getSearchPatient().toUpperCase())))
                    &&
                    (filters.getUserId() == null || (
                            appointment.getDoctor().getUser().getUserId().contains(filters.getUserId())
                    ) || appointment.getPatient().getUser().getUserId().contains(filters.getUserId()));

    private static final BiPredicate<AppointmentEty, AppointmentFilter> filter = (appointmentEty, filters) ->
            (filters.getSearch() == null || (
                    appointmentEty.getDoctor().getUser().getFirstName().toUpperCase()
                            .contains(filters.getSearch().toUpperCase())
                            || appointmentEty.getDoctor().getUser().getLastName().toUpperCase()
                            .contains(filters.getSearch().toUpperCase())
                            || appointmentEty.getPatient().getUser().getLastName().toUpperCase()
                            .contains(filters.getSearch().toUpperCase())
                            || appointmentEty.getPatient().getUser().getFirstName().toUpperCase()
                            .contains(filters.getSearch().toUpperCase())
            )) &&
                    (filters.getStartDate() == null || !appointmentEty.getDate()
                            .isBefore(filters.getStartDate())) &&
                    (filters.getEndDate() == null || !appointmentEty.getDate()
                            .isAfter(filters.getEndDate()));

    @AllArgsConstructor
    @Getter
    @Setter
    private static class AppointmentDetailsFilter {

        private LocalDate date;
        private String searchDoctor;
        private String searchPatient;
        private String userId;

    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class AppointmentFilter {
        private LocalDate startDate;
        private LocalDate endDate;
        private String search;
    }

    @Transactional
    public AppointmentEty getAppointmentById(Long appointmentId) {
        return appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getId().equals(appointmentId)).findFirst().orElse(null);
    }

    @Transactional
    public AppointmentList getAllAppointments(LocalDate startDate, LocalDate endDate, String search) {
        var appointmentList = new AppointmentList();
        var appointmentStream = appointmentRepository.findAll().stream();
        AppointmentFilter appointmentFilter;
        appointmentFilter = new AppointmentFilter(startDate, endDate, search);

        Comparator<AppointmentListItem> compareHour =
                Comparator.comparing(
                        AppointmentListItem::getHour);
        Comparator<AppointmentListItem> compareDate = Comparator.comparing(
                AppointmentListItem::getDate);

        appointmentList.setItems(
                appointmentStream
                        .filter(appointmentEty -> filter.test(appointmentEty, appointmentFilter))
                        .map(AppointmentMapper.INSTANCE::mapAppointmentEtyToAppointmentDto)
                        .sorted(compareDate.thenComparing(compareHour))
                        .collect(Collectors.toList()));

        return appointmentList;
    }

    @Transactional
    public AppointmentDetailsList getAppointmentsDetails(LocalDate date, String search) {
        var appointmentDetailsList = new AppointmentDetailsList();
        var appointmentStream = appointmentRepository.findAll().stream();
        String userId = securityAccessTokenProvider.getUserIdFromAuthToken();
        var role = securityAccessTokenProvider.getUserRoleFromAuthToken();
        AppointmentDetailsFilter appointmentDetailsFilter;
        if (role.contains("DOCTOR")) {
            appointmentDetailsFilter = new AppointmentDetailsFilter(date, null, search, userId);
        } else {
            appointmentDetailsFilter = new AppointmentDetailsFilter(date, search, null, userId);
        }

        Comparator<AppointmentDetailsListItem> compareHour =
                Comparator.comparing(
                        AppointmentDetailsListItem::getHour);
        Comparator<AppointmentDetailsListItem> compareDate = Comparator.comparing(
                AppointmentDetailsListItem::getDate);

        appointmentDetailsList.setItems(
                appointmentStream
                        .filter(appointmentEty -> filterDetails.test(appointmentEty, appointmentDetailsFilter))
                        .map(AppointmentMapper.INSTANCE::mapAppointmentEtyToAppointmentDetailsDto)
                        .sorted(compareDate.thenComparing(compareHour))
                        .collect(Collectors.toList()));

        return appointmentDetailsList;
    }

    @Transactional
    public List<String> getFreeTimeSlotForADay(LocalDate date, String doctorId, Long specializationId) {
        List<String> fixedHours = Arrays.asList("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");

        if (doctorId == null && specializationId == null)
            return fixedHours;

        if (doctorId != null) {
            var appointments = appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getDate().equals(date) && appointmentEty.getDoctor().getId().equals(doctorId)).collect(Collectors.toList());
            List<String> freeTimeSlot = new ArrayList<>();

            for (String hour : fixedHours) {
                boolean hasAppointment = false;
                for (AppointmentEty appointment : appointments) {
                    if (appointment.getHour().equals(hour)) {
                        hasAppointment = true;
                        break;
                    }
                }
                if (!hasAppointment) {
                    freeTimeSlot.add(hour);
                }
            }
            return freeTimeSlot;
        } else {
            List<String> freeTimeSlot = new ArrayList<>();
            var doctors = specializationRepository.findById(specializationId).get().getDoctorEtyList();
            for (DoctorEty doctor : doctors) {
                var appointments = appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getDate().equals(date) && appointmentEty.getDoctor().getId().equals(doctor.getId())).collect(Collectors.toList());

                for (String hour : fixedHours) {
                    boolean hasAppointment = false;
                    for (AppointmentEty appointment : appointments) {
                        if (appointment.getHour().equals(hour)) {
                            hasAppointment = true;
                            break;
                        }
                    }
                    if (!hasAppointment) {
                        if (!freeTimeSlot.contains(hour)) {
                            freeTimeSlot.add(hour);
                        }
                    }
                }
            }
            return freeTimeSlot;
        }
    }

}
