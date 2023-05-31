package ro.dental.clinic.model;

import lombok.Data;

import java.util.List;

@Data
public class TreatmentAppointmentDetailsList {
    private List<TreatmentAppointmentDetailsListItem> items;
}
