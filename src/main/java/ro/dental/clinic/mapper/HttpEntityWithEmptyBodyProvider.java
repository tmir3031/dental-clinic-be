package ro.dental.clinic.mapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HttpEntityWithEmptyBodyProvider {

    public HttpEntity<String> getEntity(String accessToken) {
        HttpHeaders httpRequestHeaders = new HttpHeaders();
        httpRequestHeaders.setBearerAuth(accessToken);
        return new HttpEntity<>(httpRequestHeaders);
    }
}
