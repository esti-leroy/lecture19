package com.tests.rest;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@DisplayName("Git issue test")
class GitIssueTest {

    private static final String REPO_NAME = "test";

    /**
     * Необходимо заполнять данным учетной записи
     */
    private static final String USERNAME = "Your username";
    private static final String PASSWORD = "Your password";
    private static final String LOGIN = "Your login";

    private static final String PREFIX = "https://api.github.com/repos";

    @BeforeAll
    static void setup() {
        if (given().get("https://api.github.com/repos/" + LOGIN + "/" + REPO_NAME)
                .andReturn().getStatusCode() != 200) {
            given()
                    .auth()
                    .preemptive()
                    .basic(USERNAME, PASSWORD)
                    .body("{\n" +
                            "  \"name\": \"" + REPO_NAME + "\"," +
                            "  \"description\": \"Test repo\"," +
                            "  \"homepage\": \"https://github.com\"," +
                            "  \"private\": false" +
                            "}")
                    .when().post("https://api.github.com/user/repos")
                    .then().statusCode(201);
            given()
                    .auth()
                    .preemptive()
                    .basic(USERNAME, PASSWORD)
                    .body("{\n" +
                            "  \"title\": \"1\",\n" +
                            "  \"state\": \"open\",\n" +
                            "  \"description\": \"Milestone v1\",\n" +
                            "  \"due_on\": \"2019-10-09T23:39:01Z\"\n" +
                            "}")
                    .when().post(PREFIX + "/" + LOGIN + "/" + REPO_NAME + "/milestones")
                    .then().statusCode(201);
        }
    }

    @Test
    @DisplayName("Test creating an issue")
    void testCreate() {
        String url = PREFIX + "/" + LOGIN + "/" + REPO_NAME + "/issues";
        Response response = given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .body("{" +
                        "  \"title\": \"Test bug\"," +
                        "  \"body\": \"Long story bug\"," +
                        "  \"milestone\": \"1\"," +
                        "  \"labels\": [" +
                        "    \"bug\"," +
                        "    \"good first issue\"" +
                        "  ]" +
                        "}")
                .when().post(url)
                .andReturn();
        assertThat(response.getStatusCode()).isEqualTo(201);
        Integer id = (Integer) response.as(Map.class).get("id");
        given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .when().get(url)
                .then().body("id", hasItems(id));
    }

    @Test
    @DisplayName("Test editing an issue")
    void testEdit() {
        String url = PREFIX + "/" + LOGIN + "/" + REPO_NAME + "/issues";
        Response response = given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .body("{" +
                        "  \"title\": \"Test bug\"," +
                        "  \"body\": \"Long story bug\"," +
                        "  \"milestone\": \"1\"," +
                        "  \"labels\": [" +
                        "    \"bug\"," +
                        "    \"good first issue\"" +
                        "  ]" +
                        "}")
                .when().post(url)
                .andReturn();
        assertThat(response.getStatusCode()).isEqualTo(201);
        Integer number = (Integer) response.as(Map.class).get("number");
        String editUrl = url + "/" + number;
        String editedBugBody = "Edited bug";
        given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .body("{" +
                        "  \"body\": \"" + editedBugBody + "\"" +
                        "}")
                .when().patch(editUrl)
                .then().statusCode(200);
        given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .when().get(url + "/" + number)
                .then().statusCode(200).body("body", equalTo(editedBugBody));
    }

    @Test
    @DisplayName("Test locking an issue")
    void testLockEdit() {
        String url = PREFIX + "/" + LOGIN + "/" + REPO_NAME + "/issues";
        Response response = given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .body("{" +
                        "  \"title\": \"Test bug\"," +
                        "  \"body\": \"Long story bug\"," +
                        "  \"milestone\": \"1\"," +
                        "  \"labels\": [" +
                        "    \"bug\"," +
                        "    \"good first issue\"" +
                        "  ]" +
                        "}")
                .when().post(url)
                .andReturn();
        assertThat(response.getStatusCode()).isEqualTo(201);
        Integer number = (Integer) response.as(Map.class).get("number");
        String lockUrl = url + "/" + number + "/lock";
        given()
                .auth()
                .preemptive()
                .basic(USERNAME, PASSWORD)
                .body("{\n" +
                        "  \"locked\": true,\n" +
                        "  \"active_lock_reason\": \"spam\"\n" +
                        "}")
                .when().put(lockUrl)
                .then().statusCode(204);
    }

}
