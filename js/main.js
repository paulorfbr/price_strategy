const pageTitles = {
  costs:       'Coûts & Rentabilité',
  strategy:    'Stratégie Tarifaire',
  pricetype:   'Type de Prix',
  positioning: 'Carte Perceptuelle',
  ansoff:      "Matrice d'Ansoff",
  synthesis:   'Synthèse',
};

// ── Render all non-canvas pages ──────────────────────────────
function renderAll() {
  renderCosts();
  renderStrategy();
  renderPriceType();
  renderSynthesis();
  if (document.getElementById('page-positioning').classList.contains('active')) {
    renderPositioning();
  }
  const stratNames = { luxury:'Luxe', penetration:'Pénétration', alignment:'Alignement', discriminatory:'Discriminatoire' };
  const typeNames  = { magic:'Magique', psychological:'Psychologique', rounded:'Arrondi' };
  document.getElementById('topbar-summary').textContent =
    `${stratNames[state.strategy]} · ${typeNames[state.pricetype]}`;
}

// ── Wire all event listeners (called after partials are loaded) ──
function initApp() {

  // Navigation
  document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', () => {
      const page = item.dataset.page;
      document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
      document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
      item.classList.add('active');
      document.getElementById('page-' + page).classList.add('active');
      document.getElementById('page-title').textContent = pageTitles[page];
      if (page === 'positioning') renderPositioning();
      if (page === 'ansoff')      renderAnsoff();
      if (page === 'synthesis')   renderSynthesis();
    });
  });

  // Cost inputs
  ['var-cost', 'fixed-cost', 'volume', 'margin', 'currency'].forEach(id => {
    document.getElementById(id).addEventListener('input', renderAll);
  });

  // Strategy cards
  document.querySelectorAll('#strategy-options .option-card').forEach(card => {
    card.addEventListener('click', () => {
      state.strategy = card.dataset.strategy;
      renderAll();
    });
  });

  // Price type cards
  document.querySelectorAll('#pricetype-options .option-card').forEach(card => {
    card.addEventListener('click', () => {
      state.pricetype = card.dataset.pricetype;
      renderAll();
    });
  });

  // Positioning inputs
  ['axis-x-left', 'axis-x-right', 'axis-y-top', 'axis-y-bottom', 'my-x', 'my-y', 'my-name'].forEach(id => {
    document.getElementById(id).addEventListener('input', () => {
      if (document.getElementById('page-positioning').classList.contains('active')) {
        renderPositioning();
      }
    });
  });

  // Ansoff matrix cells
  document.querySelectorAll('.ansoff-cell').forEach(cell => {
    cell.addEventListener('click', () => {
      state.ansoffQuadrant = cell.dataset.quadrant;
      renderAnsoff();
    });
  });

  // Resize canvas
  window.addEventListener('resize', () => {
    if (document.getElementById('page-positioning').classList.contains('active')) {
      drawPerceptualMap();
    }
  });

  // Initial render
  renderAll();
  renderAnsoff();
}

// ── Load HTML partials then boot ─────────────────────────────
async function loadPartials() {
  const parser = new DOMParser();
  const pages  = ['costs', 'strategy', 'pricetype', 'positioning', 'ansoff', 'synthesis'];
  await Promise.all(pages.map(async page => {
    const res  = await fetch(`partials/page-${page}.html`);
    const text = await res.text();
    const doc  = parser.parseFromString(text, 'text/html');
    document.getElementById(`page-${page}`).innerHTML = doc.body.innerHTML;
  }));
  initApp();
}

loadPartials();
