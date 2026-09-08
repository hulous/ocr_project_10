import { expect, test } from '@playwright/test';

test.describe.configure({ mode: 'serial' });

const CHAT_INITIALIZATION_TIMEOUT = 30000;
const BASE_URL = process.env.BASE_URL ?? 'http://127.0.0.1:4250';

async function expectChatReady(page: import('@playwright/test').Page) {
  const messageLog = page.getByRole('log', { name: 'Messages du tchat' });
  const loadingMessage = messageLog.getByText('Chargement des messages...', {
    exact: true,
  });

  await expect(messageLog).toBeVisible();
  await expect(loadingMessage).toBeVisible();
  await expect(loadingMessage).toBeHidden({
    timeout: CHAT_INITIALIZATION_TIMEOUT,
  });

  return messageLog;
}

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
  await expect(page.getByRole('log', { name: 'Messages du tchat' })).toBeVisible();
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

  const senderContext = await browser.newContext({ baseURL: BASE_URL });
  const receiverContext = await browser.newContext({ baseURL: BASE_URL });
  const senderPage = await senderContext.newPage();
  const receiverPage = await receiverContext.newPage();

  for (const [page, user] of [
    [senderPage, users[0]],
    [receiverPage, users[1]],
  ] as const) {
    const login = await request.post('/api/auth/login', {
      data: { email: user.email, password },
    });
    expect(login.ok()).toBeTruthy();
    const { token, expiresIn } = (await login.json()) as {
      token: string;
      expiresIn: number;
    };

    await page.addInitScript(
      ({ token: authenticationToken, expiresIn: authenticationDuration, email }) => {
        localStorage.setItem(
          'ycyw.authentication',
          JSON.stringify({
            token: authenticationToken,
            expiresIn: authenticationDuration,
            email,
            expiresAt: Date.now() + authenticationDuration,
          }),
        );
      },
      { token, expiresIn, email: user.email },
    );
    await page.goto('/chat');
    await expect(page).toHaveURL(/\/chat$/);
    await expect(page.getByRole('log', { name: 'Messages du tchat' })).toBeVisible();
  }

  const message = `Message temps réel ${runId}`;
  await senderPage.getByRole('textbox', { name: 'Message' }).fill(message);
  await senderPage.getByRole('button', { name: 'Envoyer' }).click();

  await expect(receiverPage.getByRole('log', { name: 'Messages du tchat' })).toContainText(
    message,
    { timeout: CHAT_INITIALIZATION_TIMEOUT },
  );
});
