# PRD — PrixStratégie

> **Outil de tarification stratégique pour PMEs IT**
> Version 1.0 · Avril 2026

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Objectifs produit](#2-objectifs-produit)
3. [Utilisateurs cibles](#3-utilisateurs-cibles)
4. [Périmètre fonctionnel](#4-périmètre-fonctionnel)
5. [Règles métier](#5-règles-métier)
6. [Exigences non-fonctionnelles](#6-exigences-non-fonctionnelles)
7. [Modèle de données](#7-modèle-de-données)
8. [Architecture technique](#8-architecture-technique)
9. [API REST](#9-api-rest)
10. [Roadmap](#10-roadmap)

---

## 1. Vue d'ensemble

### Présentation

**PrixStratégie** est une application web d'aide à la décision tarifaire conçue pour les PMEs du secteur IT. Elle guide l'utilisateur à travers un processus structuré en six étapes — des coûts de revient jusqu'à la synthèse finale — en combinant des calculs financiers, un positionnement concurrentiel et des cadres stratégiques (Ansoff).

### Proposition de valeur

| Problème | Solution apportée |
|----------|------------------|
| Fixer un prix "au feeling" sans base financière | Calcul automatique du coût complet, prix minimum et prix optimal |
| Ignorer les effets psychologiques du prix | Transformations : prix magique, psychologique, arrondi |
| Ne pas connaître son positionnement face aux concurrents | Carte perceptuelle interactive à deux axes personnalisables |
| Manque de cadre pour classer les initiatives produit | Matrice d'Ansoff avec gestion d'initiatives et analyse des risques |
| Décisions fragmentées sans vue consolidée | Page Synthèse avec validation de tous les seuils |

### Différenciateurs clés

- Fonctionne **hors ligne** (fichier `index.html` standalone, zéro dépendance)
- Calculs **temps réel** sur toutes les pages simultanément
- Support du **pricing discriminatoire** par segments (Starter / Pro / Enterprise)
- Backend **Spring Boot + PostgreSQL** optionnel pour la persistance multi-projets

---

## 2. Objectifs produit

### Objectifs v1.0

| # | Objectif | Indicateur de succès |
|---|----------|----------------------|
| O1 | Permettre le calcul du prix de revient complet | Coût unitaire, prix minimum et prix optimal calculés à partir de 4 saisies |
| O2 | Proposer 4 stratégies tarifaires configurables | Chaque stratégie produit un prix stratégique distinct et justifié |
| O3 | Appliquer 3 transformations psychologiques | Le prix final reflète le type choisi avec exemples comparatifs |
| O4 | Visualiser le positionnement face aux concurrents | Carte perceptuelle avec distance euclidienne calculée |
| O5 | Classer les initiatives via la matrice d'Ansoff | Chaque initiative classée par quadrant avec recommandations tarifaires |
| O6 | Produire une synthèse consolidée et validée | Page Synthèse avec alerte verte/orange/rouge selon les seuils |

### Métriques cibles

- Temps pour atteindre la page Synthèse depuis un démarrage à zéro : **< 5 minutes**
- Précision des calculs : **2 décimales** sur tous les montants
- Compatibilité navigateurs : Chrome, Firefox, Edge, Safari (2 dernières versions)

---

## 3. Utilisateurs cibles

### Persona principal — Le Dirigeant IT

> *"Je dois fixer le prix de notre nouvelle offre SaaS. Je connais mes coûts mais je ne sais pas quel prix pratiquer face au marché."*

- **Profil :** CEO ou CTO d'une PME IT (5–50 personnes)
- **Niveau financier :** Connaît ses coûts variables et fixes, mais n'est pas comptable
- **Besoin :** Décision rapide, justifiée, avec visualisation claire
- **Contexte d'usage :** Lancement de produit, révision annuelle de tarifs, réponse à appel d'offres

### Persona secondaire — Le Business Developer

> *"Je négocie des contrats avec des grands comptes. J'ai besoin d'une grille tarifaire segmentée et d'arguments sur le positionnement."*

- **Profil :** Commercial senior ou responsable partenariats
- **Besoin :** Pricing discriminatoire (tiers), positionnement vs concurrents, matrice Ansoff pour justifier la roadmap
- **Contexte d'usage :** Préparation de propositions commerciales, comités de pilotage

---

## 4. Périmètre fonctionnel

### 4.1 Page — Coûts & Rentabilité

**Objectif :** Définir la structure de coûts et calculer les trois seuils de prix.

#### Saisies

| Champ | Type | Contraintes | Défaut |
|-------|------|-------------|--------|
| Coût variable unitaire | Numérique | ≥ 0 | 0 |
| Coûts fixes mensuels | Numérique | ≥ 0 | 0 |
| Volume prévu (unités/mois) | Entier | ≥ 1 | 1 |
| Marge souhaitée | Pourcentage | 0–100 % | 30 % |
| Devise | Sélecteur | EUR, USD, GBP, CHF | EUR |

#### Formules de calcul

```
Coût complet unitaire  = coûtVariable + (coûtsFixes / volume)
Prix minimum           = coûtCompletUnitaire
Prix optimal           = coûtCompletUnitaire / (1 − marge/100)
```

#### Sorties affichées

- **5 KPIs** : Coût Variable, Coût Complet, Prix Minimum, Prix Optimal, Prix Final
- **Barre de prix** : visualisation linéaire des trois seuils avec zones colorées
- **Alertes dynamiques** :
  - 🔴 Prix < minimum → vente à perte
  - 🟡 Prix entre minimum et optimal → rentable mais marge non atteinte
  - 🟢 Prix ≥ optimal → objectif atteint, affichage de la marge effective

---

### 4.2 Page — Stratégie Tarifaire

**Objectif :** Choisir la stratégie de positionnement prix et la configurer.

#### Stratégies disponibles

| Stratégie | Multiplicateur | Description |
|-----------|---------------|-------------|
| **Luxe / Premium** | × 2,2 | Positionnement haut de gamme, marges élevées |
| **Pénétration** | × 1,05 | Prix agressif pour conquérir des parts de marché |
| **Alignement marché** | = prix concurrentiel | Calage sur la moyenne concurrente |
| **Discriminatoire** | Variable par segment | Segmentation par profil client |

#### Configuration spécifique par stratégie

**Alignement :** champ de saisie "Prix moyen concurrentiel" requis.

**Discriminatoire :** table de segments éditable en ligne.

| Colonne segment | Type | Règle |
|----------------|------|-------|
| Nom | Texte | Requis |
| Multiplicateur | Numérique | > 0 |
| Prix Base | Calculé | optimalPrice × multiplicateur |
| Prix Final | Calculé | applyPriceType(prixBase) |
| Statut | Indicateur | 🟢 ok / 🟡 sous optimal / 🔴 sous minimum |

Segments par défaut : Starter (×0,7), Pro (×1,0), Enterprise (×1,6).

---

### 4.3 Page — Type de Prix

**Objectif :** Appliquer une transformation psychologique au prix stratégique brut.

#### Transformations

**Prix Magique**
```
Si prix > 0 : prixFinal = floor(prix) − 0,01
Sinon       : prixFinal = 0,99
Exemple : 299,50 → 299,99
```

**Prix Psychologique**
```
Seuils = [1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000]
seuil = premier seuil > prix
Si aucun seuil trouvé : seuil = ceil(prix / 1000) × 1000
prixFinal = seuil − 3
Exemple : 1050 → 997
```

**Prix Arrondi**
```
magnitude = 10^floor(log10(prix))
pas = magnitude ≥ 1000 ? 500 : magnitude ≥ 100 ? 50 : magnitude ≥ 10 ? 5 : 1
prixFinal = round(prix / pas) × pas
Exemple : 312 → 310
```

#### Affichage requis

- Comparatif avant/après transformation (grande typographie)
- Tableau d'exemples sur 4 niveaux de prix avec variation colorée
- Alerte explicative sur l'effet psychologique du type sélectionné

---

### 4.4 Page — Carte Perceptuelle

**Objectif :** Visualiser le positionnement concurrentiel sur deux axes personnalisables.

#### Configuration des axes

| Champ | Défaut |
|-------|--------|
| Libellé gauche (X−) | Bas Prix |
| Libellé droite (X+) | Haut Prix |
| Libellé bas (Y−) | Faible Qualité |
| Libellé haut (Y+) | Haute Qualité |
| Position X de l'entreprise | 60 (0–100) |
| Position Y de l'entreprise | 75 (0–100) |
| Nom de l'entreprise | Mon Entreprise |

#### Gestion des concurrents

- Ajout / suppression dynamique
- Par concurrent : nom, position X (0–100), position Y (0–100), couleur (color picker hex)
- Représentation canvas : cercle coloré + étiquette

#### Canvas (HTML5 Canvas API)

- Grille de fond aux positions 25, 50, 75
- Flèches d'axes avec libellés
- Entreprise affichée en carré bleu pivoté à 45°
- Concurrents en cercles colorés
- Mise à jour temps réel à chaque saisie

#### Analyse du positionnement

Tableau trié par distance croissante :

| Colonne | Calcul |
|---------|--------|
| Concurrent | Nom |
| Distance | √((Δx)² + (Δy)²) |
| Écart X | myX − competitorX avec indicateur → / ← |
| Écart Y | myY − competitorY avec indicateur ↑ / ↓ |

Alerte résumée : concurrent le plus proche + région de positionnement identifiée.

---

### 4.5 Page — Matrice d'Ansoff

**Objectif :** Classer les initiatives stratégiques par quadrant et obtenir des recommandations tarifaires.

#### Quadrants

| Quadrant | Marchés | Produits | Risque | Stratégie prix recommandée |
|----------|---------|----------|--------|---------------------------|
| Pénétration de Marché | Existant | Existant | 🟢 Faible | Pénétration ou Alignement |
| Développement de Marché | Nouveau | Existant | ⚡ Moyen | Alignement ou Premium |
| Développement de Produit | Existant | Nouveau | 🔵 Moyen | Discriminatoire ou Premium |
| Diversification | Nouveau | Nouveau | 🔴 Élevé | Pénétration (lancement) |

> **Note :** Le quadrant Développement de Produit présente un risque de **cannibalisation** à signaler explicitement.

#### Gestion des initiatives

Chaque initiative contient : nom, quadrant (dropdown), description libre.

- Les initiatives sont représentées par des points colorés dans chaque quadrant
- Ajout / édition / suppression en ligne

#### Panneau latéral détail

Affiché au clic sur un quadrant :
- Nom, description, badge de risque
- Liste des 5 actions recommandées
- Recommandation tarifaire spécifique au quadrant

---

### 4.6 Page — Synthèse

**Objectif :** Valider toutes les décisions et afficher le prix final recommandé.

#### KPIs (5 cartes)

1. Prix Minimum (rouge)
2. Prix Optimal (orange)
3. Prix Final (vert)
4. Marge Effective = `(prixFinal − coûtComplet) / prixFinal × 100`
5. CA Mensuel Estimé = `prixFinal × volume`

#### Tableau récapitulatif

| Paramètre | Valeur | Note |
|-----------|--------|------|
| Stratégie Tarifaire | Nom + badge | Justification |
| Type de Prix | Nom + badge | Justification |
| Coût Variable Unitaire | Montant | — |
| Coûts Fixes Mensuels | Montant | Ventilation par unité |
| Coût Complet Unitaire | Montant | Seuil minimum |
| Marge Souhaitée | % | Prix optimal correspondant |
| **Prix Final Recommandé** | **Montant en grand** | Marge effective |

#### Validation des seuils

- 🟢 Prix ≥ optimal → marge atteinte et dépassée
- 🟡 Prix entre minimum et optimal → rentable, marge non atteinte
- 🔴 Prix < minimum → vente à perte, action requise

#### Cas discriminatoire

Affichage additionnel de chaque segment avec son prix final et son statut (✅ / ⛔).

---

## 5. Règles métier

| # | Règle | Détail |
|---|-------|--------|
| R1 | **Plancher absolu** | Le prix final ne doit jamais être en dessous du coût complet unitaire (affichage alerte rouge) |
| R2 | **Précédence des transformations** | La stratégie s'applique en premier, puis le type de prix |
| R3 | **Segments indépendants** | Chaque segment discriminatoire est validé individuellement contre le prix minimum |
| R4 | **Alignement requis** | Si stratégie = alignement, le champ prix concurrentiel est obligatoire |
| R5 | **Volume > 0** | Un volume nul rend les calculs impossibles (division par zéro) |
| R6 | **Marge 0–100 %** | Une marge ≥ 100 % est interdite (prix optimal infini) |
| R7 | **Synchronisation globale** | Tout changement de saisie déclenche un recalcul de toutes les pages actives |

---

## 6. Exigences non-fonctionnelles

| Catégorie | Exigence |
|-----------|----------|
| **Autonomie** | `index.html` standalone : zéro serveur, zéro dépendance, zéro build |
| **Performance** | Recalcul < 50 ms après toute saisie utilisateur |
| **Compatibilité** | Chrome, Firefox, Edge, Safari — 2 dernières versions majeures |
| **Responsive** | Sidebar latérale sur desktop (≥ 641 px), navigation horizontale sur mobile |
| **Accessibilité** | Contraste WCAG AA sur toutes les alertes colorées |
| **Sécurité API** | Validation Jakarta Bean Validation sur tous les DTOs ; pas d'exposition SQL |
| **Persistance** | L'état frontend est en mémoire (session). La persistance multi-projets nécessite le backend Spring Boot |

---

## 7. Modèle de données

```
pricing_project (id, name, description, created_at, updated_at)
    │
    ├─ pricing_costs        1:1   (variable_cost, fixed_cost, volume, target_margin, currency, alignment_price)
    ├─ pricing_strategy     1:1   (strategy ENUM, pricetype ENUM)
    ├─ positioning_config   1:1   (axis_x_left, axis_x_right, axis_y_top, axis_y_bottom, my_x, my_y, my_name)
    │
    ├─ price_segment        1:N   (name, multiplier, sort_order)
    ├─ competitor           1:N   (name, position_x, position_y, color, sort_order)
    └─ ansoff_initiative    1:N   (name, quadrant ENUM, description, sort_order, created_at)
```

### Enums PostgreSQL (migrés en VARCHAR après fix de compatibilité Hibernate)

| Enum | Valeurs |
|------|---------|
| `strategy_type` | luxury, penetration, alignment, discriminatory |
| `pricetype_type` | magic, psychological, rounded |
| `ansoff_quadrant` | penetration, market-dev, product-dev, diversification |

### Contraintes notables

- Toutes les clés étrangères avec `ON DELETE CASCADE`
- `position_x / position_y / my_x / my_y` : SMALLINT, CHECK 0–100
- `target_margin` : NUMERIC(5,2), CHECK 0–100
- `multiplier` : NUMERIC(5,2), CHECK > 0
- `volume` : INTEGER, CHECK > 0
- Trigger `set_updated_at()` sur `pricing_project`

---

## 8. Architecture technique

### Frontend

```
index.html (standalone)
  ├── <style>        CSS inline (variables, layout, composants)
  ├── <body>         6 sections .page (une par écran)
  └── <script>
        ├── state    Objet global de l'état applicatif
        ├── utils    calcCosts(), strategyPrice(), applyPriceType(), finalPrice()
        ├── pages/   renderCosts(), renderStrategy(), renderPriceType(),
        │            renderPositioning(), renderAnsoff(), renderSynthesis()
        └── main     initApp(), navigation, event listeners
```

**État global (JavaScript) :**
```javascript
state = {
  strategy, pricetype, alignmentPrice,
  segments: [{ name, multiplier }],
  competitors: [{ name, x, y, color }],
  ansoffQuadrant,
  ansoffInitiatives: [{ name, quadrant, description }]
}
```

### Backend (optionnel)

```
src/main/java/com/prf/prixstrategie/
  ├── entity/        7 entités JPA + 3 enums
  ├── repository/    Spring Data JPA (un repo par entité)
  ├── dto/           Java Records request/response
  ├── service/       Logique métier (@Transactional)
  └── controller/    REST controllers (/api/v1/...)
```

---

## 9. API REST

Base path : `/api/v1`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/projects` | Lister tous les projets |
| POST | `/projects` | Créer un projet |
| GET | `/projects/{id}` | Snapshot complet |
| PUT | `/projects/{id}` | Modifier nom / description |
| DELETE | `/projects/{id}` | Supprimer (cascade) |
| GET/PUT | `/projects/{id}/costs` | Coûts (upsert) |
| GET/PUT | `/projects/{id}/strategy` | Stratégie & type de prix (upsert) |
| GET/PUT | `/projects/{id}/positioning` | Config carte perceptuelle (upsert) |
| GET/POST | `/projects/{id}/competitors` | Lister / ajouter un concurrent |
| PUT/DELETE | `/projects/{id}/competitors/{cid}` | Modifier / supprimer |
| GET/POST | `/projects/{id}/segments` | Lister / ajouter un segment |
| PUT/DELETE | `/projects/{id}/segments/{sid}` | Modifier / supprimer |
| GET/POST | `/projects/{id}/ansoff` | Lister / ajouter une initiative |
| PUT/DELETE | `/projects/{id}/ansoff/{iid}` | Modifier / supprimer |

**Formats :** JSON · **Validation :** Jakarta Bean Validation sur tous les body de requête · **Codes HTTP :** 200 OK, 201 Created, 204 No Content, 404 Not Found, 400 Bad Request

---

## 10. Roadmap

### v1.1 — Persistance & Partage

- [ ] Sauvegarde d'un projet depuis le frontend (appel API)
- [ ] Export PDF / impression de la page Synthèse
- [ ] Partage par lien (URL avec project UUID)

### v1.2 — Analyses avancées

- [ ] Simulation de scénarios (comparer plusieurs combinaisons stratégie/type)
- [ ] Analyse de sensibilité (quel volume pour atteindre la marge cible ?)
- [ ] Graphique d'évolution prix/marge en fonction du volume

### v1.3 — Collaboration

- [ ] Multi-utilisateurs avec authentification (Spring Security + JWT)
- [ ] Commentaires et annotations par page
- [ ] Historique des versions d'un projet

### v2.0 — IA & Benchmarks

- [ ] Suggestions automatiques de stratégie basées sur le profil de coûts
- [ ] Import de données concurrentielles depuis des sources publiques
- [ ] Recommandation de prix par secteur (SaaS, ESN, éditeur logiciel)

---

*Document maintenu par l'équipe PRF IT Solutions · prixstrategie v1.0*
