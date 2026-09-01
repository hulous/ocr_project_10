package com.ycyw.chatapi.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class IncomingMessageDtoTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void rejectsBlankContent() {
    var violations = validator.validate(new IncomingMessageDto("demo", " "));

    assertEquals(1, violations.size());
    assertTrue(violations.stream().anyMatch(violation ->
        violation.getMessage().equals("Message content is required")));
  }
}
