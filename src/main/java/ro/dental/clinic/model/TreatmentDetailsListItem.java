package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.domain.SpecializationEty;

@Data
public class TreatmentDetailsListItem {
    private Long id;
    private SpecializationEty specialization_id;
    private String price;
    private String name;
    private Long v;

}
