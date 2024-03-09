package com.swiggy.FulfillmentService.Entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.swiggy.FulfillmentService.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryExecutiveTest {

    @Test
    public void testInitializeDeliveryExecutive_success() {
        assertDoesNotThrow(() -> validate(availableDeliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithEmptyFirstName_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().firstName(Strings.EMPTY).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithEmptyLastName_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().lastName(Strings.EMPTY).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithEmptyUsername_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().username(Strings.EMPTY).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithEmptyPassword_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().password(Strings.EMPTY).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithInvalidPassword_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().password(INVALID_PASSWORD).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithEmptyPhone_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().phone(Strings.EMPTY).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithInvalidPhoneHavingLetters_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().phone(INVALID_PHONE_HAVING_LETTERS).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithInvalidPhoneHavingLessThan10Digits_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().phone(INVALID_PHONE_HAVING_LESS_DIGITS).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    @Test
    public void testInitializeDeliveryExecutiveWithInvalidPhoneHavingMoreThan10Digits_throwsConstraintViolationException() {
        DeliveryExecutive deliveryExecutive = DeliveryExecutive.builder().phone(INVALID_PHONE_HAVING_MORE_DIGITS).build();
        assertThrows(ConstraintViolationException.class, () -> validate(deliveryExecutive));
    }

    private void validate(DeliveryExecutive deliveryExecutive) {
        Set<ConstraintViolation<DeliveryExecutive>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(deliveryExecutive);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}