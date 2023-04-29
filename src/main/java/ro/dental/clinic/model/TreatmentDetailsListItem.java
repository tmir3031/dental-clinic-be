package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.domain.SpecializationEty;

@Data
public class TreatmentDetailsListItem {
    private Long id;
    private String type;
    private String name;
    private Long price;
    private Long v;

}
