package com.swiggy.FulfillmentService.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.FulfillmentService.Exceptions.NoDeliveryExecutiveNearbyException;
import com.swiggy.FulfillmentService.Exceptions.NominatimException;
import com.swiggy.FulfillmentService.Exceptions.OrderAlreadyAssignedException;
import com.swiggy.FulfillmentService.Services.DeliveriesService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.swiggy.FulfillmentService.Constants.Constants.deliveryRequest;
import static com.swiggy.FulfillmentService.Constants.Constants.deliveryResponse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveriesControllerTest {

    @MockBean
    private DeliveriesService deliveriesService;

    @InjectMocks
    private DeliveriesController deliveriesController;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAssign_shouldAssignDeliveryExecutiveForAnOrder_returnsIsCreated() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenReturn(deliveryResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(deliveryResponse)));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
    }

    @Test
    void testAssign_whenOrderAlreadyAssignedToDeliveryExecutive_returnsConflict() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new OrderAlreadyAssignedException("Order has already been assigned to a delivery executive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Order has already been assigned to a delivery executive"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
    }

    @Test
    void testAssign_noDeliveryExecutiveFoundAvailableNearby_returnsNotFound() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new NoDeliveryExecutiveNearbyException("Cannot find any available delivery executive nearby"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cannot find any available delivery executive nearby"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
    }

    @Test
    void testAssign_errorGettingResponseFromNominatim_returnsInternalServerError() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new NominatimException("No response received from Nominatim API"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("No response received from Nominatim API"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
    }

    @Test
    void testAssign_unknownDatabaseError_returnsInternalServerError() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new RuntimeException("Error assigning delivery executive to an order"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error assigning delivery executive to an order"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
    }
}