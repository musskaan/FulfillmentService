package com.swiggy.FulfillmentService.Services;

import com.swiggy.FulfillmentService.DTOs.DeliveryResponse;
import com.swiggy.FulfillmentService.DTOs.DeliveryUpdateResponse;
import com.swiggy.FulfillmentService.DTOs.Address;
import com.swiggy.FulfillmentService.Entities.Delivery;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import com.swiggy.FulfillmentService.Enums.Availability;
import com.swiggy.FulfillmentService.Enums.DeliveryStatus;
import com.swiggy.FulfillmentService.Exceptions.*;
import com.swiggy.FulfillmentService.Repositories.DeliveriesRepository;
import com.swiggy.FulfillmentService.Repositories.DeliveryExecutivesRepository;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.swiggy.FulfillmentService.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveriesServiceTest {

    @Mock
    private DeliveriesRepository deliveriesRepository;

    @Mock
    private DeliveryExecutivesRepository deliveryExecutivesRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Delivery mockDelivery;

    @Mock
    private DeliveryExecutive mockDeliveryExecutive;

    @InjectMocks
    private DeliveriesService deliveriesService;

    private static final String jsonResponse = "<200 OK OK,[{\"place_id\":332908868,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright\",\"lat\":\"13.08114703448276\",\"lon\":\"80.26748411034482\",\"category\":\"place\",\"type\":\"postcode\",\"place_rank\":21,\"importance\":0.12000999999999995,\"addresstype\":\"postcode\",\"name\":\"452001\",\"display_name\":\"452001, Bhicholi Hapsi Tahsil, Indore District, Madhya Pradesh, India\",\"boundingbox\":[\"12.9211470\",\"13.2411470\",\"80.1074841\",\"80.4274841\"]}],[Server:\"nginx\", Date:\"Mon, 06 Mar 2024 16:29:08 GMT\", Content-Type:\"application/json; charset=utf-8\", Content-Length:\"442\", Connection:\"keep-alive\", Keep-Alive:\"timeout=20\"]>";

    @Test
    void testAssign_orderAlreadyAssigned_throwsOrderAlreadyAssignedException() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(true);

        assertThrows(OrderAlreadyAssignedException.class, () -> deliveriesService.assign(deliveryRequest));

        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, never()).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, never()).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, never()).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testAssign_receivedNullListOfAvailableDeliveryExecutives_throwsRuntimeException() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> deliveriesService.assign(deliveryRequest));

        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, never()).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, never()).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testAssign_receivedEmptyListOfAvailableDeliveryExecutives_throwsNoDeliveryExecutiveNearbyException() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(Collections.emptyList());

        assertThrows(NoDeliveryExecutiveNearbyException.class, () -> deliveriesService.assign(deliveryRequest));

        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, never()).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, never()).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testAssign_receivedNullResponseFromNominatimApi_throwsNominatimException() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(List.of(availableDeliveryExecutive));
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(null);

        assertThrows(NominatimException.class, () -> deliveriesService.assign(deliveryRequest));

        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, never()).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, never()).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testAssign_receivedEmptyResponseFromNominatimApi_throwsNominatimException() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(List.of(availableDeliveryExecutive));
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(Strings.EMPTY);

        assertThrows(NominatimException.class, () -> deliveriesService.assign(deliveryRequest));

        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, never()).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, never()).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testAssign_distanceOfDeliveryExecutiveFromRestaurantIsZero_shouldCreateADeliveryObjectAfterSuccessfulAssigning_successFullyAssigned() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(List.of(availableDeliveryExecutive));
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(jsonResponse);
        when(deliveriesRepository.save(any(Delivery.class))).thenReturn(mockDelivery);
        when(deliveryExecutivesRepository.save(availableDeliveryExecutive)).thenReturn(availableDeliveryExecutive);

        DeliveryResponse actualResponse = deliveriesService.assign(deliveryRequest);

        assertEquals(deliveryResponse, actualResponse);
        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, times(1)).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, times(1)).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testAssign_distanceOfDeliveryExecutiveFromRestaurantIsGreaterThanZero_shouldCreateADeliveryObjectAfterSuccessfulAssigning_successFullyAssigned() {
        String apiStringForDeliveryExecutive = "https://nominatim.openstreetmap.org/search?country=India&postalcode=" + ANOTHER_ZIP_CODE + "&format=json";
        String apiStringForRestaurant = "https://nominatim.openstreetmap.org/search?country=India&postalcode=" + ZIP_CODE + "&format=json";
        String jsonResponseForDeliveryExecutive = "<200 OK OK,[{\"place_id\":332908868,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright\",\"lat\":\"21.08114703448276\",\"lon\":\"90.26748411034482\",\"category\":\"place\",\"type\":\"postcode\",\"place_rank\":21,\"importance\":0.12000999999999995,\"addresstype\":\"postcode\",\"name\":\"412308\",\"display_name\":\"412308, Bhicholi Hapsi Tahsil, Indore District, Madhya Pradesh, India\",\"boundingbox\":[\"12.9211470\",\"13.2411470\",\"80.1074841\",\"80.4274841\"]}],[Server:\"nginx\", Date:\"Mon, 06 Mar 2024 16:29:08 GMT\", Content-Type:\"application/json; charset=utf-8\", Content-Length:\"442\", Connection:\"keep-alive\", Keep-Alive:\"timeout=20\"]>";
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .phone(PHONE)
                .address(new Address(STREET, CITY, STATE, ANOTHER_ZIP_CODE))
                .availability(Availability.AVAILABLE).build();
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(List.of(deliveryExecutive));
        when(restTemplate.getForObject(apiStringForRestaurant, String.class)).thenReturn(jsonResponse);
        when(restTemplate.getForObject(apiStringForDeliveryExecutive, String.class)).thenReturn(jsonResponseForDeliveryExecutive);
        when(deliveriesRepository.save(any(Delivery.class))).thenReturn(mockDelivery);
        when(deliveryExecutivesRepository.save(deliveryExecutive)).thenReturn(deliveryExecutive);

        DeliveryResponse actualResponse = deliveriesService.assign(deliveryRequest);

        assertEquals(deliveryResponse, actualResponse);
        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, times(1)).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, times(1)).save(any(DeliveryExecutive.class));
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
    }

    @Test
    void testUpdateStatus_deliveryExecutiveNotFoundInDatabase_throwsUsernameNotFoundException() {
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> deliveriesService.updateStatus(DELIVERY_ID, USERNAME));

        verify(deliveryExecutivesRepository, times(1)).findByUsername(USERNAME);
        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
        verify(deliveriesRepository, never()).save(any());
        verify(deliveryExecutivesRepository, never()).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }

    @Test
    void testUpdateStatus_deliveryNotFoundInDatabase_throwsNoSuchElementException() {
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenReturn(Optional.of(unavailableDeliveryExecutive));
        when(deliveriesRepository.findById(DELIVERY_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> deliveriesService.updateStatus(DELIVERY_ID, USERNAME));

        verify(deliveriesRepository, times(1)).findById(DELIVERY_ID);
        verify(deliveryExecutivesRepository, times(1)).findByUsername(anyString());
        verify(deliveriesRepository, never()).save(any());
        verify(deliveryExecutivesRepository, never()).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }



    @Test
    void testUpdateStatus_deliveryExecutiveNotAuthorizedToUpdateStatusOfAnotherDelivery_throwsUnauthorizedStatusUpdateException() {
        when(deliveriesRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockDeliveryExecutive));

        assertThrows(UnauthorizedStatusUpdateException.class, () -> deliveriesService.updateStatus(DELIVERY_ID, USERNAME));

        verify(deliveriesRepository, times(1)).findById(DELIVERY_ID);
        verify(deliveryExecutivesRepository, times(1)).findByUsername(USERNAME);
        verify(deliveriesRepository, never()).save(any());
        verify(deliveryExecutivesRepository, never()).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }

    @Test
    void testUpdateStatus_forDeliveryAlreadyDelivered_throwsOrderAlreadyDeliveredException() {
        when(deliveriesRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenReturn(Optional.of(unavailableDeliveryExecutive));

        assertThrows(OrderAlreadyDeliveredException.class, () -> deliveriesService.updateStatus(DELIVERY_ID, USERNAME));

        verify(deliveriesRepository, times(1)).findById(DELIVERY_ID);
        verify(deliveryExecutivesRepository, times(1)).findByUsername(USERNAME);
        verify(deliveriesRepository, never()).save(any());
        verify(deliveryExecutivesRepository, never()).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }

    @Test
    void testUpdateStatus_unexpectedDatabaseError_throwsRuntimeException() {
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenThrow(DataRetrievalFailureException.class);

        assertThrows(RuntimeException.class, () -> deliveriesService.updateStatus(DELIVERY_ID, USERNAME));

        verify(deliveriesRepository, never()).findById(DELIVERY_ID);
        verify(deliveryExecutivesRepository, times(1)).findByUsername(USERNAME);
        verify(deliveriesRepository, never()).save(any());
        verify(deliveryExecutivesRepository, never()).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }

    @Test
    void testUpdateStatus_shouldUpdateDeliveryStatusToPickedUp_successfullyUpdated() {
        when(deliveriesRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenReturn(Optional.of(unavailableDeliveryExecutive));

        DeliveryUpdateResponse actualResponse = deliveriesService.updateStatus(DELIVERY_ID, USERNAME);

        assertEquals(expectedDeliveryUpdateResponseToPickedUp, actualResponse);
        verify(deliveriesRepository, times(1)).findById(DELIVERY_ID);
        verify(deliveryExecutivesRepository, times(1)).findByUsername(USERNAME);
        verify(deliveriesRepository, times(1)).save(any());
        verify(deliveryExecutivesRepository, times(1)).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }

    @Test
    void testUpdateStatus_shouldUpdateDeliveryStatusToDeliveredAndDeliveryExecutiveStatusToAvailable_successfullyUpdated() {
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        when(deliveriesRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(deliveryExecutivesRepository.findByUsername(USERNAME)).thenReturn(Optional.of(unavailableDeliveryExecutive));

        DeliveryUpdateResponse actualResponse = deliveriesService.updateStatus(DELIVERY_ID, USERNAME);

        assertEquals(expectedDeliveryUpdateResponseToDelivered, actualResponse);
        verify(deliveriesRepository, times(1)).findById(DELIVERY_ID);
        verify(deliveryExecutivesRepository, times(1)).findByUsername(USERNAME);
        verify(deliveriesRepository, times(1)).save(any());
        verify(deliveryExecutivesRepository, times(1)).save(any());
        verify(deliveriesRepository, never()).existsByOrderId(anyLong());
    }
}