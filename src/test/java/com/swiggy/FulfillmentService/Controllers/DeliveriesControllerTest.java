package com.swiggy.FulfillmentService.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.FulfillmentService.Config.SecurityConfig;
import com.swiggy.FulfillmentService.Exceptions.*;
import com.swiggy.FulfillmentService.Services.DeliveriesService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static com.swiggy.FulfillmentService.Constants.Constants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
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

        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(deliveryResponse)));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
        verify(deliveriesService, never()).updateStatus(DELIVERY_ID, USERNAME);
    }

    @Test
    void testAssign_whenOrderAlreadyAssignedToDeliveryExecutive_returnsConflict() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new OrderAlreadyAssignedException("Order has already been assigned to a delivery executive"));

        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Order has already been assigned to a delivery executive"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
        verify(deliveriesService, never()).updateStatus(DELIVERY_ID, USERNAME);
    }

    @Test
    void testAssign_noDeliveryExecutiveFoundAvailableNearby_returnsNotFound() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new NoDeliveryExecutiveNearbyException("Cannot find any available delivery executive nearby"));

        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cannot find any available delivery executive nearby"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
        verify(deliveriesService, never()).updateStatus(DELIVERY_ID, USERNAME);
    }

    @Test
    void testAssign_errorGettingResponseFromNominatim_returnsInternalServerError() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new NominatimException("No response received from Nominatim API"));

        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("No response received from Nominatim API"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
        verify(deliveriesService, never()).updateStatus(DELIVERY_ID, USERNAME);
    }

    @Test
    void testAssign_unknownDatabaseError_returnsInternalServerError() throws Exception {
        when(deliveriesService.assign(deliveryRequest)).thenThrow(new RuntimeException("Error assigning delivery executive to an order"));

        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error assigning delivery executive to an order"));

        verify(deliveriesService, times(1)).assign(deliveryRequest);
        verify(deliveriesService, never()).updateStatus(DELIVERY_ID, USERNAME);
    }

    @Test
    void testUpdateStatusWhenNoAuthenticationProvided_returnsIsUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID)).andExpect(status().isUnauthorized());

        verify(deliveriesService, never()).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testUpdateStatusByAuthenticatedDeliveryExecutive_shouldUpdateDeliveryStatus_returnsIsOk() throws Exception {
        when(deliveriesService.updateStatus(DELIVERY_ID, USERNAME)).thenReturn(expectedDeliveryUpdateResponseToPickedUp);

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDeliveryUpdateResponseToPickedUp)));

        verify(deliveriesService, times(1)).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testUpdateStatusByAuthenticatedDeliveryExecutive_whenDeliveryNotFoundInDatabase_returnsNotFound() throws Exception {
        when(deliveriesService.updateStatus(DELIVERY_ID, USERNAME)).thenThrow(new NoSuchElementException("No delivery found with id: " + DELIVERY_ID));

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No delivery found with id: " + DELIVERY_ID));

        verify(deliveriesService, times(1)).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testUpdateStatusByUnknownDeliveryExecutive_returnsNotFound() throws Exception {
        when(deliveriesService.updateStatus(DELIVERY_ID, USERNAME)).thenThrow(new UsernameNotFoundException("No delivery executive found with given username"));

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No delivery executive found with given username"));

        verify(deliveriesService, times(1)).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testUpdateStatusByUnauthorizedDeliveryExecutive_returnsForbidden() throws Exception {
        when(deliveriesService.updateStatus(DELIVERY_ID, USERNAME)).thenThrow(new UnauthorizedStatusUpdateException("Delivery executive is not authorized to update the status of delivery: " + DELIVERY_ID));

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Delivery executive is not authorized to update the status of delivery: " + DELIVERY_ID));

        verify(deliveriesService, times(1)).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testUpdateStatusByAuthorizedDeliveryExecutive_orderAlreadyDelivered_returnsConflict() throws Exception {
        when(deliveriesService.updateStatus(DELIVERY_ID, USERNAME)).thenThrow(new OrderAlreadyDeliveredException("Cannot update status, order has already been delivered"));

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID, USERNAME))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot update status, order has already been delivered"));

        verify(deliveriesService, times(1)).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testUpdateStatusByAuthorizedDeliveryExecutive_unexpectedDatabaseError_returnsInternalServerError() throws Exception {
        when(deliveriesService.updateStatus(DELIVERY_ID, USERNAME)).thenThrow(new DataRetrievalFailureException("Error updating status for delivery: " + DELIVERY_ID));

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", DELIVERY_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error updating status for delivery: " + DELIVERY_ID));

        verify(deliveriesService, times(1)).updateStatus(DELIVERY_ID, USERNAME);
        verify(deliveriesService, never()).assign(deliveryRequest);
    }
}