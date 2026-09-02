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

test('delivers a message from one user to another in real time', async ({
  browser,
  request,
}) => {
  const runId = `${Date.now()}`;
  const users = [
    { email: `e2e-sender-${runId}@example.com`, name: 'E2E Sender' },
    { email: `e2e-receiver-${runId}@example.com`, name: 'E2E Receiver' },
  ];
  const password = 'Str0ngP@ssword';

  for (const user of users) {
    const registration = await request.post('/api/auth/register', {
      data: { ...user, password },
    });
    expect(registration.ok()).toBeTruthy();
  }

  const senderContext = await browser.newContext();
  const receiverContext = await browser.newContext();
  const senderPage = await senderContext.newPage();
  const receiverPage = await receiverContext.newPage();

  try {
    for (const [page, user] of [
      [senderPage, users[0]],
      [receiverPage, users[1]],
    ] as const) {
      await page.goto('/login');
      await page.getByLabel('Adresse email').fill(user.email);
      await page.getByLabel('Mot de passe').fill(password);
      await page.getByRole('button', { name: 'Se connecter' }).click();
      await expect(page).toHaveURL(/\/chat$/);
      await expect(page.getByRole('log', { name: 'Messages du tchat' })).toContainText(
        "Aucun message pour l'instant",
      );
    }

    const message = `Message temps réel ${runId}`;
    await senderPage.getByLabel('Message').fill(message);
    await senderPage.getByRole('button', { name: 'Envoyer' }).click();

    await expect(receiverPage.getByRole('log', { name: 'Messages du tchat' })).toContainText(
      message,
    );
  } finally {
    await Promise.all([senderContext.close(), receiverContext.close()]);
  }
});
