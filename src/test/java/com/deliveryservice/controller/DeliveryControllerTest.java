package com.deliveryservice.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Sql(value = "/GeneralRulesFee.sql")
    @Test
    void calculateDeliveryFeeTartuBike() throws Exception {
        mockMvc.perform(get("/delivery/feeRequest")
                        .param("city", "Tartu")
                        .param("vehicleType", "Bike"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }
    @Sql(value = "/GeneralRulesFee.sql")
    @Test
    void calculateDeliveryFeeTallinnCar() throws Exception {
        mockMvc.perform(get("/delivery/feeRequest")
                        .param("city", "Tallinn")
                        .param("vehicleType", "Car"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }
    @Sql(value = "/VehicleForbidden.sql")
    @Test
    void calculateDeliveryFeeBikeWithForbiddenConditions() throws Exception {
        Assertions.assertThrows(ServletException.class, () -> {
        mockMvc.perform(get("/delivery/feeRequest")
                        .param("city", "Tartu")
                        .param("vehicleType", "Bike"))
                .andExpect(status().isBadRequest());
        }, "Usage of selected vehicle type is forbidden");
    }
}
