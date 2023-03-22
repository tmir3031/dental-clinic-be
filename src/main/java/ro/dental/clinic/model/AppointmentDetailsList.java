package ro.dental.clinic.model;

import lombok.Data;

import java.util.List;

@Data
public class AppointmentDetailsList {

    private List<AppointmentDetailsListItem> items;

}
