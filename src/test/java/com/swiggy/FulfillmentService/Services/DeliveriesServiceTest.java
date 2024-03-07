package com.swiggy.FulfillmentService.Services;

import com.swiggy.FulfillmentService.DTOs.DeliveryResponse;
import com.swiggy.FulfillmentService.DTOs.Location;
import com.swiggy.FulfillmentService.Entities.Delivery;
import com.swiggy.FulfillmentService.Entities.DeliveryExecutive;
import com.swiggy.FulfillmentService.Enums.Availability;
import com.swiggy.FulfillmentService.Exceptions.NoDeliveryExecutiveNearbyException;
import com.swiggy.FulfillmentService.Exceptions.NominatimException;
import com.swiggy.FulfillmentService.Exceptions.OrderAlreadyAssignedException;
import com.swiggy.FulfillmentService.Repositories.DeliveriesRepository;
import com.swiggy.FulfillmentService.Repositories.DeliveryExecutivesRepository;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

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
    private Delivery delivery;

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
    }

    @Test
    void testAssign_distanceOfDeliveryExecutiveFromRestaurantIsZero_shouldCreateADeliveryObjectAfterSuccessfulAssigning_successFullyAssigned() {
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(List.of(availableDeliveryExecutive));
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(jsonResponse);
        when(deliveriesRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(deliveryExecutivesRepository.save(availableDeliveryExecutive)).thenReturn(availableDeliveryExecutive);

        DeliveryResponse actualResponse = deliveriesService.assign(deliveryRequest);

        assertEquals(deliveryResponse, actualResponse);
        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, times(1)).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, times(1)).save(any(DeliveryExecutive.class));
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
                .location(new Location(STREET, CITY, STATE, ANOTHER_ZIP_CODE))
                .availability(Availability.AVAILABLE).build();
        when(deliveriesRepository.existsByOrderId(ORDER_ID)).thenReturn(false);
        when(deliveryExecutivesRepository.findByAvailability(any(Availability.class))).thenReturn(List.of(deliveryExecutive));
        when(restTemplate.getForObject(apiStringForRestaurant, String.class)).thenReturn(jsonResponse);
        when(restTemplate.getForObject(apiStringForDeliveryExecutive, String.class)).thenReturn(jsonResponseForDeliveryExecutive);
        when(deliveriesRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(deliveryExecutivesRepository.save(deliveryExecutive)).thenReturn(deliveryExecutive);

        DeliveryResponse actualResponse = deliveriesService.assign(deliveryRequest);

        assertEquals(deliveryResponse, actualResponse);
        verify(deliveriesRepository, times(1)).existsByOrderId(anyLong());
        verify(deliveryExecutivesRepository, times(1)).findByAvailability(any(Availability.class));
        verify(deliveriesRepository, times(1)).save(any(Delivery.class));
        verify(deliveryExecutivesRepository, times(1)).save(any(DeliveryExecutive.class));
    }
}