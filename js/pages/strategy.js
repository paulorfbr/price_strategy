function renderStrategy() {
  document.querySelectorAll('#strategy-options .option-card').forEach(el => {
    el.classList.toggle('selected', el.dataset.strategy === state.strategy);
  });

  const { optimalPrice, minPrice } = calcCosts();
  const cur = currency();

  let configHtml = '';

  if (state.strategy === 'alignment') {
    const mp = state.alignmentPrice ?? optimalPrice;
    configHtml = `
      <div class="card">
        <div class="card-title"><span class="dot"></span> Prix du Marché (Concurrents)</div>
        <div class="form-grid">
          <div class="form-group">
            <label>Prix moyen concurrentiel</label>
            <div class="input-unit">
              <input type="number" id="alignment-price" value="${mp}" min="0" step="0.01"
                oninput="state.alignmentPrice=parseFloat(this.value)||null; renderAll();" />
              <span class="unit-label">${cur}</span>
            </div>
          </div>
        </div>
      </div>`;
  }

  if (state.strategy === 'discriminatory') {
    const rows = state.segments.map((s, i) => {
      const p      = applyPriceType(optimalPrice * s.multiplier);
      const status = p < minPrice ? '🔴 sous min' : p < optimalPrice ? '🟡 sous optimal' : '🟢 ok';
      return `<tr>
        <td><input type="text" value="${s.name}" oninput="state.segments[${i}].name=this.value; renderAll();" /></td>
        <td><input type="number" value="${s.multiplier}" min="0.1" step="0.05"
             oninput="state.segments[${i}].multiplier=parseFloat(this.value)||1; renderAll();" /></td>
        <td><strong>${fmt(optimalPrice * s.multiplier)}</strong></td>
        <td><strong>${fmt(p)}</strong></td>
        <td>${status}</td>
        <td><button class="btn-danger-sm" onclick="removeSegment(${i})">✕</button></td>
      </tr>`;
    }).join('');
    configHtml = `
      <div class="card">
        <div class="card-title" style="justify-content:space-between;">
          <span><span class="dot" style="display:inline-block;margin-right:8px;"></span>Segments de Clientèle</span>
          <button class="btn btn-outline btn-sm" onclick="addSegment()">+ Segment</button>
        </div>
        <table class="segments-table">
          <thead><tr><th>Segment</th><th>Multiplicateur</th><th>Prix Base</th><th>Prix Final</th><th>Statut</th><th></th></tr></thead>
          <tbody>${rows}</tbody>
        </table>
      </div>`;
  }

  document.getElementById('strategy-config').innerHTML = configHtml;

  const stratLabel = { luxury:'Luxe / Premium', penetration:'Pénétration', alignment:'Alignement Marché', discriminatory:'Discriminatoire' }[state.strategy];
  const stratMult  = { luxury:'×2.2', penetration:'×1.05', alignment:'= prix marché', discriminatory:'variable par segment' }[state.strategy];
  const sp = strategyPrice(optimalPrice);

  document.getElementById('strategy-impact').innerHTML = `
    <div class="kpi-row">
      <div class="kpi info">
        <div class="kpi-label">Stratégie</div>
        <div class="kpi-value" style="font-size:1rem;">${stratLabel}</div>
      </div>
      <div class="kpi info">
        <div class="kpi-label">Facteur Appliqué</div>
        <div class="kpi-value" style="font-size:1rem;">${stratMult}</div>
      </div>
      <div class="kpi warning">
        <div class="kpi-label">Prix Stratégique Brut</div>
        <div class="kpi-value">${sp.toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
      </div>
      <div class="kpi success">
        <div class="kpi-label">Prix Final (avec type)</div>
        <div class="kpi-value">${finalPrice().toLocaleString('fr-FR', {maximumFractionDigits:2})} ${cur}</div>
      </div>
    </div>
    ${sp < minPrice ? `<div class="alert alert-danger">⛔ Attention : le prix stratégique est inférieur au coût de production. Révisez vos paramètres.</div>` : ''}
  `;
}

function addSegment() {
  state.segments.push({ name: `Segment ${state.segments.length + 1}`, multiplier: 1.2 });
  renderAll();
}

function removeSegment(i) {
  if (state.segments.length <= 1) return;
  state.segments.splice(i, 1);
  renderAll();
}
