# REST API Contract: PrixStratégie v1.0

**Date**: 2026-04-10 | **Base path**: `/api/v1` | **Format**: JSON

## Global Conventions

- All request/response bodies: `Content-Type: application/json`
- Dates: ISO 8601 (`2026-04-10T14:30:00Z`)
- Monetary amounts: `number` with 2 decimal places
- Position values: `integer` 0–100
- On validation failure: `400 Bad Request` with body `{ "errors": [{ "field": "...", "message": "..." }] }`
- On missing resource: `404 Not Found` with body `{ "error": "Resource not found" }`
- Successful creation: `201 Created` with `Location` header pointing to created resource
- Successful update (no body): `204 No Content`
- Successful update (with body): `200 OK`

---

## Projects

### `GET /projects`

List all projects (summary view).

**Response 200**:
```json
[
  {
    "id": 1,
    "name": "Offre SaaS Q2",
    "description": "Tarification pour notre nouveau module RH",
    "createdAt": "2026-04-10T09:00:00Z",
    "updatedAt": "2026-04-10T09:00:00Z"
  }
]
```

---

### `POST /projects`

Create a new project. Default child records (pricing_costs, pricing_strategy,
positioning_config, and the 3 default price segments) are created automatically.

**Request body**:
```json
{
  "name": "Offre SaaS Q2",
  "description": "Tarification pour notre nouveau module RH"
}
```

Validation:
- `name`: required, 1–255 chars

**Response 201** (Location: `/api/v1/projects/1`):
```json
{
  "id": 1,
  "name": "Offre SaaS Q2",
  "description": "Tarification pour notre nouveau module RH",
  "createdAt": "2026-04-10T09:00:00Z",
  "updatedAt": "2026-04-10T09:00:00Z"
}
```

---

### `GET /projects/{id}`

Full snapshot of a project including all child records.

**Response 200**:
```json
{
  "id": 1,
  "name": "Offre SaaS Q2",
  "description": "...",
  "createdAt": "2026-04-10T09:00:00Z",
  "updatedAt": "2026-04-10T09:00:00Z",
  "costs": { /* see GET /projects/{id}/costs */ },
  "strategy": { /* see GET /projects/{id}/strategy */ },
  "positioning": { /* see GET /projects/{id}/positioning */ },
  "segments": [ /* see GET /projects/{id}/segments */ ],
  "competitors": [ /* see GET /projects/{id}/competitors */ ],
  "ansoffInitiatives": [ /* see GET /projects/{id}/ansoff */ ]
}
```

---

### `PUT /projects/{id}`

Update project name and/or description.

**Request body**:
```json
{
  "name": "Offre SaaS Q3",
  "description": "Updated description"
}
```

**Response 200**: Updated project summary (same shape as POST response).

---

### `DELETE /projects/{id}`

Delete project and all child records (CASCADE).

**Response 204**: No body.

---

## Costs

### `GET /projects/{id}/costs`

**Response 200**:
```json
{
  "variableCost": 45.00,
  "fixedCost": 3000.00,
  "volume": 100,
  "targetMargin": 30.00,
  "currency": "EUR",
  "alignmentPrice": null
}
```

---

### `PUT /projects/{id}/costs`

Upsert cost record (creates if not exists, updates if exists).

**Request body**:
```json
{
  "variableCost": 45.00,
  "fixedCost": 3000.00,
  "volume": 100,
  "targetMargin": 30.00,
  "currency": "EUR",
  "alignmentPrice": null
}
```

Validation:
- `variableCost`: required, ≥ 0
- `fixedCost`: required, ≥ 0
- `volume`: required, ≥ 1
- `targetMargin`: required, 0 ≤ x < 100
- `currency`: required, one of EUR/USD/GBP/CHF
- `alignmentPrice`: nullable, ≥ 0 if provided

**Response 200**: Full updated costs record.

---

## Strategy

### `GET /projects/{id}/strategy`

**Response 200**:
```json
{
  "strategy": "penetration",
  "priceType": "rounded"
}
```

---

### `PUT /projects/{id}/strategy`

**Request body**:
```json
{
  "strategy": "luxury",
  "priceType": "magic"
}
```

Validation:
- `strategy`: required, one of `luxury | penetration | alignment | discriminatory`
- `priceType`: required, one of `magic | psychological | rounded`

**Response 200**: Updated strategy record.

---

## Positioning

### `GET /projects/{id}/positioning`

**Response 200**:
```json
{
  "axisXLeft": "Bas Prix",
  "axisXRight": "Haut Prix",
  "axisYTop": "Haute Qualité",
  "axisYBottom": "Faible Qualité",
  "myX": 60,
  "myY": 75,
  "myName": "Mon Entreprise"
}
```

---

### `PUT /projects/{id}/positioning`

**Request body**: Same shape as GET response.

Validation:
- `axisX*`, `axisY*`, `myName`: required, 1–100 chars
- `myX`, `myY`: required, integer 0–100

**Response 200**: Updated positioning record.

---

## Price Segments

### `GET /projects/{id}/segments`

**Response 200**:
```json
[
  { "id": 1, "name": "Starter", "multiplier": 0.70, "sortOrder": 0 },
  { "id": 2, "name": "Pro",     "multiplier": 1.00, "sortOrder": 1 },
  { "id": 3, "name": "Enterprise", "multiplier": 1.60, "sortOrder": 2 }
]
```

---

### `POST /projects/{id}/segments`

**Request body**:
```json
{ "name": "Startup", "multiplier": 0.50, "sortOrder": 3 }
```

Validation:
- `name`: required, 1–100 chars
- `multiplier`: required, > 0
- `sortOrder`: optional, default 0

**Response 201**: Created segment.

---

### `PUT /projects/{id}/segments/{sid}`

**Request body**: Same as POST. **Response 200**: Updated segment.

---

### `DELETE /projects/{id}/segments/{sid}`

**Response 204**: No body.

---

## Competitors

### `GET /projects/{id}/competitors`

**Response 200**:
```json
[
  { "id": 1, "name": "Acme Corp", "positionX": 40, "positionY": 55, "color": "#e74c3c", "sortOrder": 0 }
]
```

---

### `POST /projects/{id}/competitors`

**Request body**:
```json
{ "name": "Acme Corp", "positionX": 40, "positionY": 55, "color": "#e74c3c", "sortOrder": 0 }
```

Validation:
- `name`: required, 1–100 chars
- `positionX`, `positionY`: required, integer 0–100
- `color`: required, 7-char hex string matching `#[0-9A-Fa-f]{6}`
- `sortOrder`: optional, default 0

**Response 201**: Created competitor.

---

### `PUT /projects/{id}/competitors/{cid}`

**Request body**: Same as POST. **Response 200**: Updated competitor.

---

### `DELETE /projects/{id}/competitors/{cid}`

**Response 204**: No body.

---

## Ansoff Initiatives

### `GET /projects/{id}/ansoff`

**Response 200**:
```json
[
  {
    "id": 1,
    "name": "Lancement offre SMB",
    "quadrant": "penetration",
    "description": "Renforcer nos parts de marché PME avec tarification agressive",
    "sortOrder": 0,
    "createdAt": "2026-04-10T09:00:00Z"
  }
]
```

---

### `POST /projects/{id}/ansoff`

**Request body**:
```json
{
  "name": "Lancement offre SMB",
  "quadrant": "penetration",
  "description": "Renforcer nos parts de marché PME",
  "sortOrder": 0
}
```

Validation:
- `name`: required, 1–255 chars
- `quadrant`: required, one of `penetration | market-dev | product-dev | diversification`
- `description`: optional
- `sortOrder`: optional, default 0

**Response 201**: Created initiative.

---

### `PUT /projects/{id}/ansoff/{iid}`

**Request body**: Same as POST. **Response 200**: Updated initiative.

---

### `DELETE /projects/{id}/ansoff/{iid}`

**Response 204**: No body.

---

## Error Response Format

```json
{
  "timestamp": "2026-04-10T09:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "errors": [
    { "field": "volume", "message": "must be greater than or equal to 1" },
    { "field": "targetMargin", "message": "must be less than 100" }
  ],
  "path": "/api/v1/projects/1/costs"
}
```
