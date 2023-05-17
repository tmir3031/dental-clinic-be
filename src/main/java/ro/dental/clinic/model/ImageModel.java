package ro.dental.clinic.model;

import lombok.Data;

import java.time.Instant;

@Data
public class ImageModel {
    private byte[] image;
    private Instant date;
}
