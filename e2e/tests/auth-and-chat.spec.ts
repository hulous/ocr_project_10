import { expect, test } from '@playwright/test';

test('redirects unauthenticated visitors to login', async ({ page }) => {
  await page.goto('/chat');

  await expect(page).toHaveURL(/\/login\?returnUrl=%2Fchat$/);
  await expect(page.getByRole('heading', { name: 'Retrouvez votre espace client.' })).toBeVisible();
});

test('registers through the API, logs in, and opens the chat', async ({ page, request }) => {
  const email = `e2e-${Date.now()}@example.com`;
  const password = 'Str0ngP@ssword';

  const registration = await request.post('/api/auth/register', {
    data: { email, password, name: 'E2E User' },
  });
  expect(registration.ok()).toBeTruthy();

  await page.goto('/login');
  await page.getByLabel('Adresse email').fill(email);
  await page.getByLabel('Mot de passe').fill(password);
  await page.getByRole('button', { name: 'Se connecter' }).click();

  await expect(page).toHaveURL(/\/chat$/);
  await expect(page.getByRole('heading', { name: 'Tchat', exact: true })).toBeVisible();
  await expect(page.getByRole('log', { name: 'Messages du tchat' })).toContainText(
    "Aucun message pour l'instant",
  );
});
