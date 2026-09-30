# Security changes and production rollout

This is source-code hardening, not a certification that the application has no vulnerabilities. No production database, AWS account, Cloudflare account, Stripe account, or Cloudinary account was accessed or changed during this review.

## What changed

- Every servlet service imports `security-common`. Bearer tokens require a valid HS256 signature, subject, and expiry. Current identity records determine account status and roles, so suspensions and role removals take effect on the next authenticated request. Identity failures deny the request.
- Internal endpoints require an independent service secret. The gateway blocks internal paths, private payment operations, encoded/ambiguous paths, and actuator endpoints, and strips spoofed service headers. Direct service ports are no longer published by Compose. Only the gateway is published, on host loopback.
- Ownership checks protect listings, orders, reservations, notifications, follows, seller onboarding, community content, reviews, and verification records. Business verification is an admin operation. Invalidated business profiles lose business roles. Community visibility and anonymous-review privacy are enforced on the server.
- Payment confirmation retrieves the PaymentIntent from Stripe and checks success, received amount, currency, order, and seller. Browser-supplied payment IDs alone cannot mark an order paid. Intent creation, refunds, and transfers use stable idempotency keys. Unsigned webhooks cannot change orders. Public onboarding redirects cannot mint another user's onboarding link.
- Order transitions use MongoDB operation locks across replicas. Stock increments/decrements are atomic and keyed by operation, so a retry does not apply the same adjustment twice. Listing versions prevent ordinary listing saves from overwriting concurrent stock changes. Reservation ownership cannot be reassigned by updating a document.
- Password hashes, pickup secrets, and payment client secrets are omitted from generic responses. Public profiles omit private contact details, addresses, and preferences. The dedicated buyer pickup-code endpoint remains protected.
- New verification documents use authenticated Cloudinary storage and five-minute download URLs. Images are size/type/dimension checked. Avatar/community image uploads now go through an authenticated backend endpoint instead of a public unsigned upload preset.
- Login, registration, authenticated writes, image uploads, verification submissions, and pickup attempts have bounded per-instance limits. Passwords require at least 12 characters and at most 72 UTF-8 bytes at registration. Existing shorter passwords can still log in. Usernames have a unique index. Search text is escaped before regex matching.
- Spring Boot/Cloud and Cloudinary dependencies were updated. Docker images run as an unprivileged user. CI tests and builds source before rebuilding deployment images; the previous workflow could restart old images without shipping source fixes. Dependabot checks for future updates.
- The obsolete tracked `order-app.jar`, which embedded a hardcoded database URI, was removed. Historical copies may still exist in Git, old images, and downloads.

## Before production deployment

1. **Rotate exposed or reused secrets.** Check the database credential represented by the historical `order-app.jar` connection string. Rotate it if it was real. Rotate JWT and internal-service secrets, especially if either used a checked-in default. Use two independent values from `openssl rand -hex 32`; never copy example/test secrets. Old JWTs will stop working after rotation. Git history is not scrubbed by this patch.
2. Set all environment variables in `.env.example` on the host. Every service now requires `JWT_SECRET` and `INTERNAL_SHARED_SECRET`. Profile service also needs Cloudinary credentials. Keep `.env` out of Git, image layers, logs, and build artifacts. Back up the deployment configuration before replacing Compose files.
3. Deploy the services together with the supplied root Compose configuration. The host HTTPS reverse proxy must forward to `127.0.0.1:9000`. A reverse proxy in another container should use `api-gateway:9000` on a shared private network. Do not reopen ports 8081–8091. Keep the AWS firewall closed to those ports and to MongoDB. Configure MongoDB access lists/private networking and separate least-privilege database users; these account settings cannot be enforced by repository files.
4. Back up MongoDB and check for duplicate usernames/emails before enabling identity's unique indexes. Duplicate existing records must be reconciled before rollout. Listing startup initializes missing version fields to zero; this is an idempotent database migration. Drain old service instances first so old code cannot overwrite new inventory metadata.
5. **Migrate existing verification documents.** Older public Cloudinary URLs are deliberately no longer returned. Make the original assets private/authenticated, invalidate public cached copies, and update each stored `documentUrl` to `private:<public_id>:<format>`, or ask the owner to resubmit. Do not merely hide the URL: previously public copies may remain accessible until the provider invalidates them. New documents are private by default. Disable the old unsigned upload preset in Cloudinary after deploying the frontend.
6. Configure the frontend production `VITE_API_BASE_URL` as the actual HTTPS API URL. Its build generates CSP/security headers for that origin. Restrict gateway CORS to `https://www.locallyl.com,https://locallyl.com`; explicitly add `http://localhost:5173` only in development. Frontend sessions require a fresh login after upgrading from persistent local-storage tokens.
7. Use a staging database and Stripe test keys to test login, registration with an avatar, business approval/rejection, NGO claims, checkout, signed webhooks, pickup, refunds, admin operations, private-document downloads, and following-only posts before production rollout. Test browser CORS/CSP and the host reverse proxy. No live payment or cloud integration test was performed here.

## Failure recovery and limits

- `operation_locks` in the order database intentionally has no automatic expiry. A process crash can leave a lock. Stop/reconcile the affected operation against Stripe, the order, and listing `inventoryOperations`; only then remove that specific stale lock. Never bulk-delete locks while requests run. Multi-item checkout or provider failures still require reconciliation; this change is not a cross-service transaction engine.
- Inventory operation IDs are retained for replay protection. Monitor document growth and archive settled-operation history only with a deliberate retention/reconciliation design. Stripe idempotency has provider-defined retention; it does not replace persistent order state.
- Application rate limits are per process and reset on restart. Add Cloudflare/WAF or a distributed limiter for multi-replica and IP-based protection, particularly login/register and uploads. Never trust arbitrary client `X-Forwarded-For` headers.
- Service credentials are shared inside the private network. This limits public access but is not isolation against a compromised trusted service. Separate service identities/mTLS and per-operation authorization are further defense-in-depth work.
- Browser tokens are now session-scoped, but JavaScript can still access them; this is not an HttpOnly-cookie session design. Logout clears the browser token, not every already-issued copy. Do not treat frontend roles or decoded JWTs as authorization.
- Continue dependency scanning and patching. The Spring 3.x generation needs an ongoing supported-platform upgrade plan. A successful compile and a clean npm audit do not prove the whole stack is vulnerability-free.

## Validation

From the repository root, with Java 21:

```bash
./gradlew securityRegression compileJava
./gradlew bootJar
```

`securityRegression` runs isolated `*SecurityTest` suites with mocked database/provider calls; it does not run the older environment-dependent `contextLoads` tests. The security tests cover forged/expired tokens, current account roles/status, internal credentials, gateway boundaries, cross-user access, private profile fields, payment verification, webhook signatures, image validation, and rate limits.

Provider references: [Stripe payment status verification](https://docs.stripe.com/payments/payment-intents/verifying-status), [Stripe idempotent requests](https://docs.stripe.com/api/idempotent_requests), [Cloudinary access controls](https://cloudinary.com/documentation/control_access_to_media), [Spring Cloud compatibility](https://spring.io/projects/spring-cloud/).
