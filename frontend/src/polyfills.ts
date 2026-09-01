/** Polyfills needed by Angular and is loaded before the app. */
import 'zone.js';

const browserGlobal = globalThis as typeof globalThis & { global?: typeof globalThis };
browserGlobal.global ??= globalThis;
