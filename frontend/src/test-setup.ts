import "@angular/compiler";
import "@analogjs/vitest-angular/setup-zone";
import { expect, vi, type Mock } from "vitest";
import { COMPILER_OPTIONS } from "@angular/core";
import { DOCUMENT } from "@angular/common";
import { getTestBed } from "@angular/core/testing";
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting,
} from "@angular/platform-browser-dynamic/testing";

getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting([
    { provide: DOCUMENT, useValue: document },
    { provide: COMPILER_OPTIONS, useValue: {}, multi: true },
  ]),
);

type JasmineSpy = Mock & {
  and: {
    returnValue: (value: unknown) => JasmineSpy;
  };
  calls: {
    mostRecent: () => { args: unknown[] };
    reset: () => void;
  };
};

Object.assign(globalThis, {
  jasmine: {
    createSpy: (name: string) => createJasmineSpy(name),
    createSpyObj: (name: string, methods: string[]) =>
      Object.fromEntries(
        methods.map((method) => [
          method,
          createJasmineSpy(`${name}.${method}`),
        ]),
      ),
    objectContaining: (value: unknown) =>
      expect.objectContaining(value as object),
    any: (constructor: unknown) =>
      expect.any(constructor as abstract new (...args: never[]) => unknown),
  },
});

function createJasmineSpy(name: string) {
  const spy = vi.fn().mockName(name) as JasmineSpy;
  spy.and = {
    returnValue: (value: unknown) => {
      spy.mockReturnValue(value);
      return spy;
    },
  };
  spy.calls = {
    mostRecent: () => ({ args: spy.mock.lastCall ?? [] }),
    reset: () => spy.mockClear(),
  };
  return spy;
}

expect.extend({
  toBeFalse(received: unknown) {
    return {
      pass: received === false,
      message: () => `expected ${received} to be false`,
    };
  },
});
