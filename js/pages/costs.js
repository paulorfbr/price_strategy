function renderCosts() {
  const { varCost, fixedCost, volume, margin, costPerUnit, minPrice, optimalPrice } = calcCosts();
  const cur = currency();

  document.getElementById('cost-kpis').innerHTML = `
    <div class="kpi info">
      <div class="kpi-label">Coût Variable Unitaire</div>
      <div class="kpi-value">${varCost.toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi info">
      <div class="kpi-label">Coût Complet Unitaire</div>
      <div class="kpi-value">${costPerUnit.toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi danger">
      <div class="kpi-label">Prix Minimum</div>
      <div class="kpi-value">${minPrice.toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi warning">
      <div class="kpi-label">Prix Optimal (${margin}%)</div>
      <div class="kpi-value">${optimalPrice.toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi success">
      <div class="kpi-label">Prix Recommandé Final</div>
      <div class="kpi-value">${finalPrice().toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
    </div>
  `;

  const fp  = finalPrice();
  const max = Math.max(optimalPrice * 3, fp * 1.1);
  const toW = (v) => Math.min(100, (v / max) * 100).toFixed(1);

  document.getElementById('price-bar-viz').innerHTML = `
    <div style="margin-bottom:50px;">
      <div style="font-size:0.78rem;color:var(--muted);margin-bottom:6px;font-weight:600;">Échelle des prix (rapport aux seuils de rentabilité)</div>
      <div class="price-bar-track">
        <div class="price-bar-fill" style="width:${toW(fp)}%;background:${fp < minPrice ? 'var(--danger)' : fp < optimalPrice ? 'var(--warning)' : 'var(--success)'};"></div>
        <div class="price-bar-marker" style="left:${toW(minPrice)}%;background:var(--danger);">
          <span class="price-bar-label">${minPrice.toFixed(0)} ${cur}<br/>Min</span>
        </div>
        <div class="price-bar-marker" style="left:${toW(optimalPrice)}%;background:var(--warning);">
          <span class="price-bar-label">${optimalPrice.toFixed(0)} ${cur}<br/>Optimal</span>
        </div>
        <div class="price-bar-marker" style="left:${toW(fp)}%;background:var(--primary);width:4px;">
          <span class="price-bar-label" style="color:var(--primary);font-weight:800;">${fp.toFixed(0)} ${cur}<br/>Final</span>
        </div>
      </div>
    </div>
  `;

  let alerts = '';
  if (fp < minPrice) {
    alerts += `<div class="alert alert-danger">⛔ Le prix final (${fmt(fp)}) est en dessous du prix minimum de rentabilité (${fmt(minPrice)}). Vous perdez de l'argent.</div>`;
  } else if (fp < optimalPrice) {
    alerts += `<div class="alert alert-warning">⚠️ Le prix final (${fmt(fp)}) est en dessous du prix optimal (${fmt(optimalPrice)}). La marge souhaitée de ${margin}% n'est pas atteinte.</div>`;
  } else {
    alerts += `<div class="alert alert-success">✅ Le prix final (${fmt(fp)}) est au-dessus du seuil de rentabilité. Marge effective : ${(((fp - costPerUnit) / fp) * 100).toFixed(1)}%.</div>`;
  }
  document.getElementById('cost-alerts').innerHTML = alerts;
}
