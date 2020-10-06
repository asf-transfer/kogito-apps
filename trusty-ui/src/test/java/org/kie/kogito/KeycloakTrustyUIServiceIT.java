package org.kie.kogito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.kie.kogito.testcontainers.KogitoKeycloakContainer;
import org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(KeycloakTrustyUIServiceIT.ITKeycloakProfile.class)
class KeycloakTrustyUIServiceIT {

    private static final String VALID_USER = "jdoe";
    private static final String TRUSTY_UI_ENDPOINT = "/";

    @ConfigProperty(name = KeycloakQuarkusTestResource.KOGITO_KEYCLOAK_PROPERTY)
    String keycloakURL;

    @Test
    void shouldReturnUnauthorized() {
        given().get(TRUSTY_UI_ENDPOINT)
                .then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void shouldReturnOkWhenValidUser() {
        given().auth().oauth2(getAccessToken(VALID_USER)).get(TRUSTY_UI_ENDPOINT)
                .then().statusCode(HttpStatus.SC_OK);
    }

    private String getAccessToken(String userName) {
        return given().param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", KogitoKeycloakContainer.CLIENT_ID)
                .param("client_secret", KogitoKeycloakContainer.CLIENT_SECRET)
                .when()
                .post(keycloakURL + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

    public static class ITKeycloakProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Collections.emptyMap();
        }

        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Collections.emptySet();
        }

        @Override
        public List<TestResourceEntry> testResources() {
            return Collections
                    .singletonList(new TestResourceEntry(KeycloakQuarkusTestResource.class));
        }
    }
}
