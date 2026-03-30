const ANSOFF = {
  penetration: {
    name: 'Pénétration de Marché', icon: '📈', riskLabel: 'Faible',
    riskBg: '#dcfce7', riskColor: '#166534', cannibalization: false,
    pricingStrategy: 'Pénétration / Alignement',
    description: 'Vendre davantage de vos produits/services <strong>existants</strong> sur votre <strong>marché actuel</strong>. Stratégie à faible risque — vous maîtrisez le produit et le marché.',
    actions: [
      'Réduire les prix pour conquérir des clients concurrents',
      'Intensifier les actions marketing et relances commerciales',
      'Programmes de fidélisation (contracts long terme, SLA premium)',
      'Augmenter la fréquence d\'utilisation (nouvelles licences, upsell)',
      'Acquisitions de petits concurrents sur le même marché',
    ],
    pricing: 'Prix compétitif ou agressif pour gagner des parts. Stratégie de pénétration ou d\'alignement marché recommandée.',
  },
  'market-dev': {
    name: 'Développement de Marché', icon: '🌍', riskLabel: 'Moyen',
    riskBg: '#fef9c3', riskColor: '#854d0e', cannibalization: false,
    pricingStrategy: 'Alignement / Premium selon marché',
    description: 'Introduire vos <strong>produits existants</strong> sur de <strong>nouveaux marchés</strong> (géographies, nouveaux secteurs, nouveaux canaux). Risque modéré — le produit est connu.',
    actions: [
      'Expansion géographique (nouvelles régions, pays)',
      'Ciblage de nouveaux secteurs d\'activité (ex: santé, finance)',
      'Nouveaux canaux de distribution (revendeurs, marketplaces)',
      'Localisation / adaptation légère de l\'offre',
      'Partenariats stratégiques pour accéder au nouveau marché',
    ],
    pricing: 'Prix à adapter au pouvoir d\'achat du nouveau marché. Un prix d\'entrée attractif peut accélérer l\'adoption.',
  },
  'product-dev': {
    name: 'Développement de Produit', icon: '🔧', riskLabel: 'Moyen',
    riskBg: '#dbeafe', riskColor: '#1e40af', cannibalization: true,
    pricingStrategy: 'Discriminatoire / Premium',
    description: 'Développer de <strong>nouvelles offres</strong> pour votre <strong>marché actuel</strong>. Risque de <strong>cannibalisation</strong> : les nouveaux produits peuvent réduire les ventes des anciens.',
    actions: [
      'Nouvelles fonctionnalités ou modules complémentaires (add-ons)',
      'Version Next-Gen remplaçant progressivement l\'offre actuelle',
      'Bundles et packages enrichis (upsell naturel)',
      'R&D sur innovations incrémentales demandées par les clients',
      'Migration assistée des clients vers la nouvelle offre',
    ],
    pricing: 'Tarification discriminatoire par version (Starter / Pro / Enterprise) pour éviter la cannibalisation. Prix premium si innovation différenciante.',
  },
  diversification: {
    name: 'Diversification', icon: '🎲', riskLabel: 'Élevé',
    riskBg: '#fee2e2', riskColor: '#991b1b', cannibalization: false,
    pricingStrategy: 'Pénétration (lancement)',
    description: 'Lancer de <strong>nouveaux produits</strong> sur de <strong>nouveaux marchés</strong>. Stratégie la plus risquée — ni le produit ni le marché ne sont maîtrisés. Requiert ressources importantes.',
    actions: [
      'Diversification connexe (domaines proches du cœur de métier)',
      'Diversification conglomérale (nouveaux domaines sans lien)',
      'Acquisitions stratégiques dans de nouveaux secteurs',
      'Joint-ventures ou partenariats pour partager le risque',
      'MVP et tests marché avant déploiement complet',
    ],
    pricing: 'Prix de lancement bas (pénétration) pour forcer l\'adoption, puis augmentation progressive. Valider le consentement à payer avant de fixer le prix définitif.',
  },
};

function renderAnsoff() {
  document.querySelectorAll('.ansoff-cell').forEach(el => {
    el.classList.toggle('active', el.dataset.quadrant === state.ansoffQuadrant);
  });

  ['penetration', 'market-dev', 'product-dev', 'diversification'].forEach(q => {
    const el = document.getElementById('dots-' + q);
    if (!el) return;
    el.innerHTML = state.ansoffInitiatives
      .filter(i => i.quadrant === q)
      .map(i => `<div class="ansoff-dot" title="${i.name}"></div>`)
      .join('');
  });

  renderAnsoffDetail();
  renderAnsoffInitiativesList();
  renderAnsoffRisk();
}

function renderAnsoffDetail() {
  const d = ANSOFF[state.ansoffQuadrant];
  if (!d) return;
  const actionsHtml = d.actions.map(a => `<li style="margin-bottom:5px;">${a}</li>`).join('');
  document.getElementById('ansoff-detail').innerHTML = `
    <div style="font-size:2rem;margin-bottom:8px;">${d.icon}</div>
    <div style="font-weight:800;font-size:1rem;color:var(--text);margin-bottom:6px;">${d.name}</div>
    <div style="margin-bottom:10px;display:flex;flex-wrap:wrap;gap:5px;">
      <span class="tag" style="background:${d.riskBg};color:${d.riskColor};">Risque ${d.riskLabel}</span>
      ${d.cannibalization ? '<span class="tag tag-amber">⚠️ Cannibalisation</span>' : ''}
    </div>
    <p style="font-size:0.82rem;line-height:1.55;margin-bottom:12px;">${d.description}</p>
    <div style="font-size:0.72rem;font-weight:700;color:var(--muted);text-transform:uppercase;margin-bottom:6px;">Actions Recommandées</div>
    <ul style="font-size:0.79rem;line-height:1.5;padding-left:16px;margin-bottom:12px;">${actionsHtml}</ul>
    <div style="padding:10px 12px;background:#f0f9ff;border-left:3px solid var(--primary);border-radius:0 6px 6px 0;">
      <div style="font-size:0.7rem;font-weight:700;color:var(--primary);text-transform:uppercase;margin-bottom:3px;">💰 Stratégie Tarifaire Liée</div>
      <div style="font-size:0.8rem;color:var(--text);font-weight:600;margin-bottom:3px;">${d.pricingStrategy}</div>
      <div style="font-size:0.78rem;color:var(--muted);">${d.pricing}</div>
    </div>
  `;
}

function renderAnsoffInitiativesList() {
  const el = document.getElementById('ansoff-initiatives-list');
  if (state.ansoffInitiatives.length === 0) {
    el.innerHTML = '<p style="color:var(--muted);font-size:0.85rem;text-align:center;padding:20px 0;">Aucune initiative. Cliquez sur "+ Ajouter".</p>';
    return;
  }
  const qOpts = [
    ['penetration',     '📈 Pénétration de Marché'],
    ['market-dev',      '🌍 Développement de Marché'],
    ['product-dev',     '🔧 Développement de Produit'],
    ['diversification', '🎲 Diversification'],
  ];
  const rows = state.ansoffInitiatives.map((ini, i) => {
    const d    = ANSOFF[ini.quadrant];
    const opts = qOpts.map(([v, l]) =>
      `<option value="${v}" ${ini.quadrant === v ? 'selected' : ''}>${l}</option>`
    ).join('');
    return `<tr>
      <td><input type="text" value="${ini.name}"
           oninput="state.ansoffInitiatives[${i}].name=this.value; renderAnsoff();" /></td>
      <td>
        <select onchange="state.ansoffInitiatives[${i}].quadrant=this.value; renderAnsoff();" style="width:100%;">
          ${opts}
        </select>
      </td>
      <td>
        <span class="tag" style="background:${d.riskBg};color:${d.riskColor};">${d.riskLabel}</span>
        ${d.cannibalization ? '<span class="tag tag-amber" style="margin-left:3px;">⚠️ Cannibal.</span>' : ''}
      </td>
      <td><input type="text" value="${ini.description}" placeholder="Note…"
           oninput="state.ansoffInitiatives[${i}].description=this.value;" /></td>
      <td><button class="btn-danger-sm" onclick="removeAnsoffInitiative(${i})">✕</button></td>
    </tr>`;
  }).join('');
  el.innerHTML = `
    <table class="segments-table">
      <thead><tr><th>Initiative / Produit</th><th>Quadrant</th><th>Risque</th><th>Note</th><th></th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
}

function renderAnsoffRisk() {
  const el  = document.getElementById('ansoff-risk');
  const byQ = {};
  ['penetration', 'market-dev', 'product-dev', 'diversification'].forEach(q => {
    byQ[q] = state.ansoffInitiatives.filter(i => i.quadrant === q);
  });
  const total = state.ansoffInitiatives.length;

  let html = `
    <div class="kpi-row">
      <div class="kpi success"><div class="kpi-label">📈 Pénétration</div><div class="kpi-value">${byQ['penetration'].length}</div></div>
      <div class="kpi warning"><div class="kpi-label">🌍 Dev. Marché</div><div class="kpi-value">${byQ['market-dev'].length}</div></div>
      <div class="kpi info"><div class="kpi-label">🔧 Dev. Produit</div><div class="kpi-value">${byQ['product-dev'].length}</div></div>
      <div class="kpi danger"><div class="kpi-label">🎲 Diversification</div><div class="kpi-value">${byQ['diversification'].length}</div></div>
    </div>`;

  if (byQ['product-dev'].length > 0) {
    const names = byQ['product-dev'].map(i => `<strong>${i.name}</strong>`).join(', ');
    html += `<div class="alert alert-warning" style="margin-top:4px;">
      ⚠️ <strong>Risque de Cannibalisation :</strong> ${names}
      cible(nt) le marché existant avec un nouveau produit. Segmentez clairement les offres et utilisez une
      <strong>tarification discriminatoire</strong> pour protéger les revenus actuels.
    </div>`;
  }

  if (byQ['diversification'].length > 0) {
    html += `<div class="alert alert-danger" style="margin-top:6px;">
      🔴 <strong>Exposition Élevée — Diversification :</strong>
      ${byQ['diversification'].length} initiative(s) sur marchés et produits inconnus.
      Budgets R&amp;D, marketing et validation marché importants nécessaires.
      Envisagez des partenariats ou un MVP pour limiter le risque.
    </div>`;
  }

  if (total > 0) {
    const dominant = Object.entries(byQ).sort((a, b) => b[1].length - a[1].length)[0][0];
    const d = ANSOFF[dominant];
    html += `<div class="alert alert-info" style="margin-top:6px;">
      💡 <strong>Recommandation Principale :</strong> Votre portefeuille est dominé par
      <strong>${d.icon} ${d.name}</strong>. ${d.pricing}
    </div>`;
  }

  html += `
    <div style="margin-top:16px;">
      <table class="synth-table">
        <thead>
          <tr><th>Quadrant</th><th>Risque</th><th>Cannibalisation</th><th>Stratégie Tarifaire</th><th>Vos Initiatives</th></tr>
        </thead>
        <tbody>
          ${Object.entries(ANSOFF).map(([q, d]) => `
            <tr style="${state.ansoffQuadrant === q ? 'background:#eff6ff;font-weight:600;' : ''}">
              <td>${d.icon} ${d.name}</td>
              <td><span class="tag" style="background:${d.riskBg};color:${d.riskColor};">${d.riskLabel}</span></td>
              <td>${d.cannibalization ? '<span class="tag tag-amber">⚠️ Oui</span>' : '<span class="tag tag-green">Non</span>'}</td>
              <td><span class="tag tag-blue">${d.pricingStrategy}</span></td>
              <td>${byQ[q].length
                ? byQ[q].map(i => `<span class="tag tag-blue" style="margin:1px 2px;">${i.name}</span>`).join('')
                : '<span style="color:var(--muted);font-weight:400;">—</span>'}</td>
            </tr>`).join('')}
        </tbody>
      </table>
    </div>`;

  el.innerHTML = html;
}

function addAnsoffInitiative() {
  state.ansoffInitiatives.push({
    name:        `Initiative ${state.ansoffInitiatives.length + 1}`,
    quadrant:    state.ansoffQuadrant || 'penetration',
    description: '',
  });
  renderAnsoff();
}

function removeAnsoffInitiative(i) {
  state.ansoffInitiatives.splice(i, 1);
  renderAnsoff();
}
