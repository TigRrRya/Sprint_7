package org.example.steps;

import io.qameta.allure.Step;
import org.example.model.Courier;

import java.util.UUID;

import static org.example.constants.ConstantsApiAndUrl.BASE_NAME;
import static org.example.constants.ConstantsApiAndUrl.BASE_PASSWORD;

public class StepGenerationCourier extends BaseApi {

    @Step("Генерация уникального курьера")
    public Courier generateUniqueCourier() {
        String uniqueLogin = "user-" + UUID.randomUUID().toString().substring(0, 8);
        return new Courier(uniqueLogin, BASE_PASSWORD, BASE_NAME);
    }

    @Step("Генерация случайного логина")
    public String generateRandomLogin() {
        return "fake-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Step("Генерация случайного пароля")
    public String generateRandomPassword() {
        return "pass-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
