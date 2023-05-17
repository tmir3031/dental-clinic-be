package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ro.dental.clinic.domain.Entitate;
import ro.dental.clinic.domain.EntitateRepository;
import ro.dental.clinic.domain.PatientEty;
import ro.dental.clinic.domain.PatientRepository;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.ImageModel;
import ro.dental.clinic.utils.TimeManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final EntitateRepository entitateRepository;

    private final TimeManager timeManager;
    private final PatientRepository patientRepository;

    @Transactional
    public void createEty(MultipartFile image, String userId) {
        var entitate = new Entitate();
        String folder = "src/main/resources/photos/";
        try {
            byte[] bytes = image.getBytes();
            var instant = timeManager.instant();
            entitate.setDate(instant);
            entitate.setPatient(patientRepository.findById(userId).get());
            entitate.setImageURL(folder + image.getOriginalFilename());

            var savedEntitate = entitateRepository.save(entitate);
            entitate.setImageURL(folder + savedEntitate.getId());

            entitateRepository.save(entitate);
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
            var image = entitateRepository.getAllByPatient(patient).get(0);
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
            myImageModel.setDate(image.getDate());

        } catch (IOException e) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.IMAGE_NOT_EXIST).build()));
        }
        return myImageModel;
    }

//    @Transactional
//    public List<ImageModel> getImages(String userId) {
//        var imageModels = new ArrayList<ImageModel>();
//        try {
//            var patient = patientRepository.findById(userId).get();
//            var images = entitateRepository.getAllByPatient(patient);
//            for (var image : images) {
//                URL url = new URL("file:./" + image.getImageURL());
//                ByteArrayOutputStream output = new ByteArrayOutputStream();
//                URLConnection conn = url.openConnection();
//                try (InputStream inputStream = conn.getInputStream()) {
//                    int n = 0;
//                    byte[] buffer = new byte[1024];
//                    while (-1 != (n = inputStream.read(buffer))) {
//                        output.write(buffer, 0, n);
//                    }
//                }
//                byte[] img = output.toByteArray();
//                var myImageModel = new ImageModel();
//                myImageModel.setImage(img);
//                myImageModel.setDate(image.getDate());
//                imageModels.add(myImageModel);
//            }
//        } catch (IOException e) {
//            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.IMAGE_NOT_EXIST).build()));
//        }
//        return imageModels;
//    }

    @Transactional
    public List<ImageModel> getImages(String userId) {
        List<ImageModel> imageModels = new ArrayList<>();
        PatientEty patient = patientRepository.findById(userId).orElseThrow(() -> new RuntimeException("Pacientul cu id-ul " + userId + " nu exista."));
        List<Entitate> images = entitateRepository.getAllByPatient(patient);

        imageModels = images.stream()
                .sorted(Comparator.comparing(Entitate::getDate).reversed()) // Sortare după dată
                .map(image -> {
                    try {
                        URL url = new URL("file:./" + image.getImageURL());
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        URLConnection conn = url.openConnection();
                        try (InputStream inputStream = conn.getInputStream()) {
                            int n;
                            byte[] buffer = new byte[1024];
                            while (-1 != (n = inputStream.read(buffer))) {
                                output.write(buffer, 0, n);
                            }
                        }
                        byte[] img = output.toByteArray();
                        ImageModel myImageModel = new ImageModel();
                        myImageModel.setImage(img);
                        myImageModel.setDate(image.getDate());
                        return myImageModel;
                    } catch (IOException e) {
                        throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.IMAGE_NOT_EXIST).build()));
                    }
                })
                .collect(Collectors.toList());
        return imageModels;
    }

}
