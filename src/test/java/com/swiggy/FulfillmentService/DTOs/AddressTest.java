package com.swiggy.FulfillmentService.DTOs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.swiggy.FulfillmentService.Constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    public void testInitializeAddress_success() {
        assertDoesNotThrow(() -> validate(address));
    }

    @Test
    public void testInitializeAddressWithEmptyStreet_throwsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> validate(new Address(Strings.EMPTY, CITY, STATE, ZIP_CODE)));
    }

    @Test
    public void testInitializeAddressWithEmptyCity_throwsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> validate(new Address(STREET, Strings.EMPTY, STATE, ZIP_CODE)));
    }

    @Test
    public void testInitializeAddressWithEmptyState_throwsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> validate(new Address(STREET, CITY, Strings.EMPTY, ZIP_CODE)));
    }

    @Test
    public void testInitializeAddressWithEmptyZipCode_throwsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> validate(new Address(STREET, CITY, STATE, Strings.EMPTY)));
    }

    @Test
    public void testInitializeAddressInvalidZipCode_throwsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> validate(new Address(STREET, CITY, STATE, INVALID_ZIP_CODE)));
    }

    private void validate(Address address) {
        Set<ConstraintViolation<Address>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(address);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}