function renderPriceType() {
  document.querySelectorAll('#pricetype-options .option-card').forEach(el => {
    el.classList.toggle('selected', el.dataset.pricetype === state.pricetype);
  });

  const { optimalPrice } = calcCosts();
  const cur       = currency();
  const stratBase = strategyPrice(optimalPrice);
  const fp        = applyPriceType(stratBase);

  const typeDesc = {
    magic:         'Réduit le prix de 0,01 pour obtenir un prix en X,99 — très utilisé en e-commerce et SaaS.',
    psychological: 'Place le prix juste en dessous d\'un palier symbolique (100, 500, 1 000...). Effet de "bon deal".',
    rounded:       'Prix entier net, perçu comme plus sérieux et professionnel. Recommandé pour les offres B2B IT.',
  }[state.pricetype];

  const examplesRaw = [stratBase * 0.7, stratBase * 0.85, stratBase, stratBase * 1.1];
  const rows = examplesRaw.map(p => {
    const after = applyPriceType(p);
    const diff  = after - p;
    return `<tr>
      <td>${p.toLocaleString('fr-FR', {minimumFractionDigits:2, maximumFractionDigits:2})} ${cur}</td>
      <td>→</td>
      <td><strong>${after.toLocaleString('fr-FR', {minimumFractionDigits:2, maximumFractionDigits:2})} ${cur}</strong></td>
      <td style="color:${diff >= 0 ? 'var(--success)' : 'var(--danger)'};font-weight:600;">${diff >= 0 ? '+' : ''}${diff.toLocaleString('fr-FR', {minimumFractionDigits:2, maximumFractionDigits:2})} ${cur}</td>
    </tr>`;
  }).join('');

  document.getElementById('price-transform').innerHTML = `
    <div class="alert alert-info" style="margin-bottom:16px;">💡 ${typeDesc}</div>
    <div style="display:flex;align-items:center;gap:32px;margin-bottom:20px;flex-wrap:wrap;">
      <div style="text-align:center;">
        <div style="font-size:0.75rem;color:var(--muted);font-weight:700;text-transform:uppercase;margin-bottom:4px;">Prix Stratégique Brut</div>
        <div style="font-size:2rem;font-weight:800;color:var(--muted);">${stratBase.toLocaleString('fr-FR', {minimumFractionDigits:2, maximumFractionDigits:2})} ${cur}</div>
      </div>
      <div style="font-size:2rem;color:var(--primary);">→</div>
      <div style="text-align:center;">
        <div style="font-size:0.75rem;color:var(--primary);font-weight:700;text-transform:uppercase;margin-bottom:4px;">Prix Final Appliqué</div>
        <div style="font-size:2.4rem;font-weight:900;color:var(--primary);">${fp.toLocaleString('fr-FR', {minimumFractionDigits:2, maximumFractionDigits:2})} ${cur}</div>
      </div>
    </div>
    <div style="font-size:0.82rem;color:var(--muted);margin-bottom:8px;font-weight:700;">Exemples à différents niveaux de prix :</div>
    <table class="segments-table">
      <thead><tr><th>Prix Brut</th><th></th><th>Après Transformation</th><th>Variation</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>
  `;
}
