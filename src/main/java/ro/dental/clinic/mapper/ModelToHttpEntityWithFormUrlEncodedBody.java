package ro.dental.clinic.mapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class ModelToHttpEntityWithFormUrlEncodedBody {

    public HttpEntity<MultiValueMap<String, String>> getRequest(MultiValueMap<String, String> requestBody) {

        HttpHeaders httpRequestHeaders = new HttpHeaders();
        httpRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(requestBody, httpRequestHeaders);
    }
}