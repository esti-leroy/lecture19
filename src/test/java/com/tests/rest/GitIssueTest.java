package com.tests.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

@DisplayName("Git issue test")
public class GitIssueTest {

    @BeforeAll
    public static void setup() {
        given().auth().preemptive().basic("esti.leroy@gmail.com", "EstiL666").body("{\n" +
                "  \"name\": \"test\",\n" +
                "  \"description\": \"Test repo\",\n" +
                "  \"homepage\": \"https://github.com\",\n" +
                "  \"private\": true\n" +
                "}").when().post("https://api.github.com/user/repos").then().statusCode(201);
        //post("https://api.github.com/user/repos")
    }

    @Test
    public void test() {
        System.out.println("lol");
    }

}
