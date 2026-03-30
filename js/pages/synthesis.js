function renderSynthesis() {
  const { varCost, fixedCost, volume, margin, costPerUnit, minPrice, optimalPrice } = calcCosts();
  const cur      = currency();
  const fp       = finalPrice();
  const margEff  = ((fp - costPerUnit) / fp * 100).toFixed(1);
  const stratNames = { luxury:'Luxe / Premium', penetration:'Pénétration', alignment:'Alignement Marché', discriminatory:'Discriminatoire' };
  const typeNames  = { magic:'Prix Magique (X,99)', psychological:'Prix Psychologique', rounded:'Prix Arrondi' };

  document.getElementById('synth-kpis').innerHTML = `
    <div class="kpi danger">
      <div class="kpi-label">Prix Minimum</div>
      <div class="kpi-value">${minPrice.toLocaleString('fr-FR',{maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi warning">
      <div class="kpi-label">Prix Optimal</div>
      <div class="kpi-value">${optimalPrice.toLocaleString('fr-FR',{maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi success">
      <div class="kpi-label">Prix Final</div>
      <div class="kpi-value">${fp.toLocaleString('fr-FR',{maximumFractionDigits:2})} ${cur}</div>
    </div>
    <div class="kpi ${parseFloat(margEff) >= margin ? 'success' : 'warning'}">
      <div class="kpi-label">Marge Effective</div>
      <div class="kpi-value">${margEff}%</div>
    </div>
    <div class="kpi info">
      <div class="kpi-label">CA Mensuel Estimé</div>
      <div class="kpi-value">${(fp * volume).toLocaleString('fr-FR',{maximumFractionDigits:0})} ${cur}</div>
    </div>
  `;

  document.getElementById('synth-table').innerHTML = `
    <thead>
      <tr><th>Paramètre</th><th>Valeur</th><th>Note</th></tr>
    </thead>
    <tbody>
      <tr><td>Stratégie Tarifaire</td><td><span class="tag tag-blue">${stratNames[state.strategy]}</span></td><td>${{luxury:'Positionnement haut de gamme', penetration:'Conquête de parts de marché', alignment:'Compétitivité par l\'alignement', discriminatory:'Segmentation de la clientèle'}[state.strategy]}</td></tr>
      <tr><td>Type de Prix</td><td><span class="tag tag-purple">${typeNames[state.pricetype]}</span></td><td>${{magic:'Effet marketing attractif', psychological:'Franchissement psychologique de seuil', rounded:'Sérieux et clarté B2B'}[state.pricetype]}</td></tr>
      <tr><td>Coût Variable Unitaire</td><td>${varCost.toFixed(2)} ${cur}</td><td>Coût direct par unité livrée</td></tr>
      <tr><td>Coûts Fixes Mensuels</td><td>${fixedCost.toFixed(2)} ${cur}</td><td>Répartis sur ${volume} unités = ${(fixedCost/volume).toFixed(2)} ${cur}/u</td></tr>
      <tr><td>Coût Complet Unitaire</td><td>${costPerUnit.toFixed(2)} ${cur}</td><td>Seuil de rentabilité absolue</td></tr>
      <tr><td>Marge Souhaitée</td><td>${margin}%</td><td>Prix Optimal : ${optimalPrice.toFixed(2)} ${cur}</td></tr>
      <tr><td><strong>Prix Final Recommandé</strong></td>
          <td><strong style="color:var(--primary);font-size:1.1em;">${fp.toLocaleString('fr-FR',{minimumFractionDigits:2,maximumFractionDigits:2})} ${cur}</strong></td>
          <td>Marge effective : ${margEff}%</td></tr>
    </tbody>
  `;

  const recColor = fp >= optimalPrice ? 'var(--success)' : fp >= minPrice ? 'var(--warning)' : 'var(--danger)';
  document.getElementById('final-price-recommendation').innerHTML = `
    <div style="text-align:center;padding:24px 0;">
      <div style="font-size:0.8rem;color:var(--muted);font-weight:700;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:8px;">Prix Final Recommandé</div>
      <div style="font-size:3.5rem;font-weight:900;color:${recColor};">${fp.toLocaleString('fr-FR',{minimumFractionDigits:2,maximumFractionDigits:2})} ${cur}</div>
      <div style="margin-top:8px;display:flex;gap:8px;justify-content:center;flex-wrap:wrap;">
        <span class="tag tag-blue">${stratNames[state.strategy]}</span>
        <span class="tag tag-purple">${typeNames[state.pricetype]}</span>
        <span class="tag ${parseFloat(margEff)>=margin?'tag-green':'tag-amber'}">Marge ${margEff}%</span>
      </div>
    </div>
  `;

  let validHtml = '';
  if (fp >= optimalPrice) {
    validHtml += `<div class="alert alert-success">✅ <strong>Prix au-dessus de l'optimal :</strong> La marge de ${margin}% est atteinte et dépassée (${margEff}% effectif).</div>`;
  } else if (fp >= minPrice) {
    validHtml += `<div class="alert alert-warning">⚠️ <strong>Prix entre minimum et optimal :</strong> Vous êtes rentable mais la marge souhaitée de ${margin}% n'est pas atteinte (seulement ${margEff}%).</div>`;
  } else {
    validHtml += `<div class="alert alert-danger">⛔ <strong>Prix en dessous du minimum :</strong> Vous vendez à perte. Coût complet : ${fmt(costPerUnit)}. Ajustez votre stratégie.</div>`;
  }

  if (state.strategy === 'discriminatory') {
    const segRows = state.segments.map(s => {
      const sp = applyPriceType(optimalPrice * s.multiplier);
      const ok = sp >= minPrice;
      return `<li>${s.name} : <strong>${fmt(sp)}</strong> ${ok ? '✅' : '⛔ sous minimum'}</li>`;
    }).join('');
    validHtml += `<div class="alert alert-info" style="margin-top:10px;">🎚️ <strong>Tarification discriminatoire :</strong><ul style="margin-top:6px;padding-left:18px;">${segRows}</ul></div>`;
  }

  document.getElementById('synth-validation').innerHTML = validHtml;
}
