package ro.dental.clinic.model;

import lombok.Data;

import java.util.List;

@Data
public class TreatmentDetailsList {
    private List<TreatmentDetailsListItem> items;
}
