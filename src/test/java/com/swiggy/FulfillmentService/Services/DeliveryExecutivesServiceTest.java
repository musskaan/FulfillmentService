package com.swiggy.FulfillmentService.Services;

import com.swiggy.FulfillmentService.DTOs.DeliveryExecutiveRegistrationResponse;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import com.swiggy.FulfillmentService.Repositories.DeliveryExecutivesRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.swiggy.FulfillmentService.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryExecutivesServiceTest {

    @Mock
    private DeliveryExecutivesRespository deliveryExecutivesRespository;

    @Mock
    private DeliveryExecutive deliveryExecutive;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DeliveryExecutivesService deliveryExecutivesService;

    @Test
    void testRegisterDeliveryExecutive_usernameAlreadyExists_throwsIllegalArgumentException() {
        when(deliveryExecutivesRespository.existsByUsername(deliveryExecutiveRegistrationRequest.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest));

        verify(deliveryExecutivesRespository, times(1)).existsByUsername(deliveryExecutiveRegistrationRequest.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(deliveryExecutivesRespository, never()).save(any(DeliveryExecutive.class));
    }

    @Test
    void testRegisterDeliveryExecutive_unknownDatabaseErrorWhileSaving_throwsRuntimeException() {
        when(deliveryExecutivesRespository.existsByUsername(deliveryExecutiveRegistrationRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(deliveryExecutiveRegistrationRequest.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(deliveryExecutivesRespository.save(any(DeliveryExecutive.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest));

        verify(deliveryExecutivesRespository, times(1)).existsByUsername(deliveryExecutiveRegistrationRequest.getUsername());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(deliveryExecutivesRespository, times(1)).save(any(DeliveryExecutive.class));
    }

    @Test
    void testRegisterDeliveryExecutive_successfulRegistration() {
        DeliveryExecutiveRegistrationResponse expectedResponse = DeliveryExecutiveRegistrationResponse.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .phone(PHONE)
                .location(location)
                .message(SUCCESSFULLY_REGISTERED)
                .build();
        when(deliveryExecutivesRespository.existsByUsername(deliveryExecutiveRegistrationRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(deliveryExecutiveRegistrationRequest.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(deliveryExecutivesRespository.save(any(DeliveryExecutive.class))).thenReturn(deliveryExecutive);

        DeliveryExecutiveRegistrationResponse actualResponse = deliveryExecutivesService.register(deliveryExecutiveRegistrationRequest);

        assertEquals(expectedResponse, actualResponse);
        verify(deliveryExecutivesRespository, times(1)).existsByUsername(deliveryExecutiveRegistrationRequest.getUsername());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(deliveryExecutivesRespository, times(1)).save(any(DeliveryExecutive.class));
    }
}