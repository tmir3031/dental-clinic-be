package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ro.dental.clinic.domain.Entitate;
import ro.dental.clinic.domain.EntitateRepository;
import ro.dental.clinic.domain.PatientRepository;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.ImageModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final EntitateRepository repository;
    private final PatientRepository patientRepository;

    @Transactional
    public void createEty(MultipartFile image) {
        var entitate = new Entitate();
        String folder = "src/main/resources/photos/";
        try {
            byte[] bytes = image.getBytes();
            entitate.setImageURL(folder + image.getOriginalFilename());

            var savedEntitate = repository.save(entitate);
            entitate.setImageURL(folder + savedEntitate.getId());

            repository.save(entitate);
            Path path = Paths.get(folder + savedEntitate.getId());
            Files.write(path, bytes);

        } catch (IOException e) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.IMAGE_NOT_SAVED)
                    .build()));
        }
    }

    @Transactional
    public ImageModel getImage(String userId) {
        var myImageModel = new ImageModel();
        try {
            var patient = patientRepository.findById(userId).get();
            var image = repository.getByPatient(patient);
            URL url = new URL("file:./" + image.getImageURL());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            URLConnection conn = url.openConnection();
            try (InputStream inputStream = conn.getInputStream()) {
                int n = 0;
                byte[] buffer = new byte[1024];
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            }
            byte[] img = output.toByteArray();
            myImageModel.setImage(img);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myImageModel;
    }
}
