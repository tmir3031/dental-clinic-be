package ro.dental.clinic.config;

import javax.annotation.Nullable;

public interface BearerTokenProvider {


    /**
     * @return the provided bearer token; might be null
     */
    @Nullable
    String getBearerToken();

}
