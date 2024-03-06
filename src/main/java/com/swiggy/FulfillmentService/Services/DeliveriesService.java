package com.swiggy.FulfillmentService.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.FulfillmentService.Builders.Builder;
import com.swiggy.FulfillmentService.DTOs.DeliveryRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.swiggy.FulfillmentService.Constants.Constants.*;

@Service
@RequiredArgsConstructor
public class DeliveriesService {

    private final DeliveriesRepository deliveriesRepository;

    private final DeliveryExecutivesRepository deliveryExecutivesRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeliveryResponse assign(DeliveryRequest deliveryRequest) {
        try {
            if (deliveriesRepository.existsByOrderId(deliveryRequest.getOrderId())) {
                throw new OrderAlreadyAssignedException("This order has already been assigned to another delivery executive");
            }

            DeliveryExecutive closestAvailableDeliveryExecutive = getNearestAvailableDeliveryExecutive(deliveryRequest.getPickupLocation());

            if (closestAvailableDeliveryExecutive == null) {
                throw new NoDeliveryExecutiveNearbyException("No delivery executive is available at the moment");
            }

            closestAvailableDeliveryExecutive.setAvailability(Availability.UNAVAILABLE);
            Delivery delivery = Builder.buildDelivery(closestAvailableDeliveryExecutive, deliveryRequest);

            deliveriesRepository.save(delivery);
            deliveryExecutivesRepository.save(closestAvailableDeliveryExecutive);

            return Builder.buildDeliveryResponse(closestAvailableDeliveryExecutive, deliveryRequest);
        } catch (OrderAlreadyAssignedException | NoDeliveryExecutiveNearbyException | NominatimException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error assigning delivery partner to the order: " + deliveryRequest.getOrderId(), e);
        }
    }

    private DeliveryExecutive getNearestAvailableDeliveryExecutive(Location location) throws JsonProcessingException {
        List<DeliveryExecutive> availableExecutives = findAvailableDeliveryExecutives();
        double[] restaurantLocation = getCoordinatesFromNominatim(location);

        double closestDistance = Double.MAX_VALUE;
        DeliveryExecutive closestAvailableDeliveryExecutive = null;

        for (DeliveryExecutive availableDeliveryExecutive : availableExecutives) {
            double[] deliveryExecutiveLocation = getCoordinatesFromNominatim(availableDeliveryExecutive.getLocation());
            double distance = calculateDistance(restaurantLocation[0], restaurantLocation[1], deliveryExecutiveLocation[0], deliveryExecutiveLocation[1]);

            if (distance == 0) {
                closestAvailableDeliveryExecutive = availableDeliveryExecutive;
                break;
            }

            if (distance < closestDistance) {
                closestDistance = distance;
                closestAvailableDeliveryExecutive = availableDeliveryExecutive;
            }
        }

        return closestAvailableDeliveryExecutive;
    }

    private List<DeliveryExecutive> findAvailableDeliveryExecutives() {
        return deliveryExecutivesRepository.findByAvailability(Availability.AVAILABLE);
    }

    private double[] getCoordinatesFromNominatim(Location location) throws JsonProcessingException {
        String addressResponse = getNominatimResponse(location);
        JsonNode restaurantAddressJson = objectMapper.readTree(addressResponse);
        double latitude = restaurantAddressJson.get(LATITUDE).asDouble();
        double longitude = restaurantAddressJson.get(LONGITUDE).asDouble();
        return new double[] {latitude, longitude};
    }

    private String getNominatimResponse(Location location) {
        String url = String.format(NOMINATIM_URL_FORMAT, location.getZipcode());
        String response = restTemplate.getForObject(url, String.class);

        if (response == null || response.isEmpty()) {
            throw new NominatimException("No response received from Nominatim API");
        }

        return extractAddressData(response);
    }

    private String extractAddressData(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("[");
        int endIndex = jsonResponse.lastIndexOf("}]");
        return jsonResponse.substring(startIndex + 1, endIndex + 1);
    }

    // Great Circle Distance formula to calculate distance between two geographic coordinates
    private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double phi1 = Math.toRadians(latitude1);
        double lambda1 = Math.toRadians(longitude1);
        double phi2 = Math.toRadians(latitude2);
        double lambda2 = Math.toRadians(longitude2);

        double deltaPhi = phi2 - phi1;
        double deltaLambda = lambda2 - lambda1;

        double a = Math.pow(Math.sin(deltaPhi / 2), 2) + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin(deltaLambda / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double EARTH_RADIUS = 6371.0; // Earth radius in kilometers
        return EARTH_RADIUS * c;
    }
}
