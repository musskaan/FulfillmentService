package com.swiggy.FulfillmentService.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.FulfillmentService.Services.DeliveryExecutivesService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.swiggy.FulfillmentService.Constants.Constants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryExecutivesControllerTest {

    @MockBean
    private DeliveryExecutivesService deliveryExecutivesService;

    @InjectMocks
    private DeliveryExecutivesController deliveryExecutivesController;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testRegisterDeliveryExecutive_usernameAlreadyExists_returnsBadRequest() throws Exception {
        when(deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest)).thenThrow(new IllegalArgumentException("User with the given username already exists"));

        mockMvc.perform(post("/api/v1/deliveryExecutives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryExecutiveRegistrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with the given username already exists"));

        verify(deliveryExecutivesService, times(1)).register(deliveryExecutiveRegistrationRequest);
    }

    @Test
    public void testRegisterDeliveryExecutive_unknownRepositoryError_returnsInternalServerError() throws Exception {
        when(deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest)).thenThrow(new DataRetrievalFailureException("Error saving to database"));

        mockMvc.perform(post("/api/v1/deliveryExecutives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryExecutiveRegistrationRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error saving to database"));

        verify(deliveryExecutivesService, times(1)).register(deliveryExecutiveRegistrationRequest);
    }

    @Test
    public void testRegisterDeliveryExecutive_successfulRegistration_returnsIsCreated() throws Exception {
        when(deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest)).thenReturn(deliveryExecutiveRegistrationResponse);

        mockMvc.perform(post("/api/v1/deliveryExecutives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryExecutiveRegistrationRequest)))
                .andExpect(status().isCreated()).
                andExpect(jsonPath("$.firstName").value(deliveryExecutiveRegistrationResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(deliveryExecutiveRegistrationResponse.getLastName()))
                .andExpect(jsonPath("$.username").value(deliveryExecutiveRegistrationResponse.getUsername()))
                .andExpect(jsonPath("$.phone").value(deliveryExecutiveRegistrationResponse.getPhone()))
                .andExpect(jsonPath("$.location").value(deliveryExecutiveRegistrationResponse.getLocation()))
                .andExpect(jsonPath("$.message").value(SUCCESSFULLY_REGISTERED));

        verify(deliveryExecutivesService, times(1)).register(deliveryExecutiveRegistrationRequest);
    }
}