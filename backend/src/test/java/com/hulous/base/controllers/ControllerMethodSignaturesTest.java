package com.hulous.base.controllers;

import com.hulous.base.responses.ApiMessageResponse;
import com.hulous.base.responses.LoginResponse;

import com.hulous.base.responses.UserResponse;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ControllerMethodSignaturesTest {

  @Test
  void authenticationsControllerMethodSignaturesAreStable() throws Exception {
    assertResponseEntityPayloadType(AuthenticationsController.class, "registrate", UserResponse.class);
    assertResponseEntityPayloadType(AuthenticationsController.class, "authenticate", LoginResponse.class);
    assertResponseEntityPayloadType(AuthenticationsController.class, "authenticatedUser", UserResponse.class);
  }

  @Test
  void usersControllerMethodSignaturesAreStable() throws Exception {
    assertResponseEntityPayloadType(UsersController.class, "show", UserResponse.class);
  }

  private static void assertResponseEntityPayloadType(Class<?> controllerClass, String methodName,
                                                      Class<?> expectedPayloadClass) throws Exception {
    Method targetMethod = findMethodByName(controllerClass, methodName);

    assertEquals(ResponseEntity.class, targetMethod.getReturnType(),
      "Expected return type ResponseEntity for " + controllerClass.getSimpleName() + "." + methodName);

    Type genericReturnType = targetMethod.getGenericReturnType();
    assertInstanceOf(ParameterizedType.class, genericReturnType,
      "Expected parameterized return type for " + controllerClass.getSimpleName() + "." + methodName);

    ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
    Type payloadType = parameterizedType.getActualTypeArguments()[0];
    assertEquals(expectedPayloadClass, payloadType,
      "Unexpected ResponseEntity payload type for " + controllerClass.getSimpleName() + "." + methodName);
  }

  private static Method findMethodByName(Class<?> controllerClass, String methodName) {
    for (Method method : controllerClass.getDeclaredMethods()) {
      if (method.getName().equals(methodName)) {
        return method;
      }
    }

    throw new IllegalStateException(
      "Method not found: " + controllerClass.getSimpleName() + "." + methodName
    );
  }
}
