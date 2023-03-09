package ro.dental.clinic.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ModelToHttpEntityWithJsonBody {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    public HttpEntity<String> getEntity(String accessToken, Object body) {
        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.setContentType(MediaType.APPLICATION_JSON);
        deleteHeaders.setBearerAuth(accessToken);

        String jsonDeleteStr = null;

        try {
            jsonDeleteStr = jsonMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonDeleteStr, deleteHeaders);

        return httpEntity;
    }
}
