function renderPositioning() {
  document.getElementById('lbl-x-left').textContent   = document.getElementById('axis-x-left').value;
  document.getElementById('lbl-x-right').textContent  = document.getElementById('axis-x-right').value;
  document.getElementById('lbl-y-top').textContent    = document.getElementById('axis-y-top').value;
  document.getElementById('lbl-y-bottom').textContent = document.getElementById('axis-y-bottom').value;

  const listEl = document.getElementById('competitors-list');
  listEl.innerHTML = state.competitors.map((c, i) => `
    <div class="competitor-row">
      <input type="color" value="${c.color}" title="Couleur"
             oninput="state.competitors[${i}].color=this.value; renderPositioning();" />
      <div style="flex:1;display:flex;flex-direction:column;gap:4px;">
        <input type="text" value="${c.name}" placeholder="Nom du concurrent"
               oninput="state.competitors[${i}].name=this.value; renderPositioning();" />
        <div style="display:flex;gap:6px;align-items:center;">
          <label style="font-size:0.7rem;text-transform:none;letter-spacing:0;white-space:nowrap;">X (0–100)</label>
          <input type="number" value="${c.x}" min="0" max="100" style="width:64px;"
                 oninput="state.competitors[${i}].x=parseFloat(this.value)||50; renderPositioning();" />
          <label style="font-size:0.7rem;text-transform:none;letter-spacing:0;white-space:nowrap;">Y (0–100)</label>
          <input type="number" value="${c.y}" min="0" max="100" style="width:64px;"
                 oninput="state.competitors[${i}].y=parseFloat(this.value)||50; renderPositioning();" />
        </div>
      </div>
      <button class="btn-danger-sm" onclick="removeCompetitor(${i})" style="align-self:flex-start;">✕</button>
    </div>`).join('');

  drawPerceptualMap();
  renderPositioningAnalysis();
}

function drawPerceptualMap() {
  const wrap   = document.getElementById('perceptual-canvas-wrap');
  const canvas = document.getElementById('perceptual-canvas');
  const W = wrap.clientWidth  || 600;
  const H = wrap.clientHeight || W * 0.56;
  canvas.width  = W;
  canvas.height = H;
  const ctx = canvas.getContext('2d');

  const pad = 48;
  const iW  = W - pad * 2;
  const iH  = H - pad * 2;

  ctx.clearRect(0, 0, W, H);

  // Grid
  ctx.strokeStyle = '#e2e8f0';
  ctx.lineWidth = 1;
  for (let i = 1; i < 4; i++) {
    const gx = pad + (iW / 4) * i;
    const gy = pad + (iH / 4) * i;
    ctx.beginPath(); ctx.moveTo(gx, pad);      ctx.lineTo(gx, pad + iH); ctx.stroke();
    ctx.beginPath(); ctx.moveTo(pad, gy);      ctx.lineTo(pad + iW, gy); ctx.stroke();
  }

  // Axes
  ctx.strokeStyle = '#94a3b8';
  ctx.lineWidth = 2;
  ctx.beginPath(); ctx.moveTo(pad, H / 2);   ctx.lineTo(pad + iW, H / 2); ctx.stroke();
  ctx.beginPath(); ctx.moveTo(W / 2, pad);   ctx.lineTo(W / 2, pad + iH); ctx.stroke();

  // Arrow tips
  ctx.fillStyle = '#94a3b8';
  ctx.beginPath(); ctx.moveTo(pad+iW+6,H/2); ctx.lineTo(pad+iW-6,H/2-5); ctx.lineTo(pad+iW-6,H/2+5); ctx.fill();
  ctx.beginPath(); ctx.moveTo(W/2,pad-6);    ctx.lineTo(W/2-5,pad+6);     ctx.lineTo(W/2+5,pad+6);    ctx.fill();

  function plotDot(x100, y100, color, label, size = 10, isSelf = false) {
    const cx = pad + (x100 / 100) * iW;
    const cy = pad + ((100 - y100) / 100) * iH;

    if (isSelf) {
      ctx.beginPath();
      ctx.arc(cx, cy, size + 4, 0, Math.PI * 2);
      ctx.fillStyle = color + '33';
      ctx.fill();
      ctx.save();
      ctx.translate(cx, cy);
      ctx.rotate(Math.PI / 4);
      ctx.fillStyle = color;
      ctx.fillRect(-size / 2, -size / 2, size, size);
      ctx.restore();
    } else {
      ctx.beginPath();
      ctx.arc(cx, cy, size, 0, Math.PI * 2);
      ctx.fillStyle = color;
      ctx.fill();
      ctx.strokeStyle = '#fff';
      ctx.lineWidth = 2;
      ctx.stroke();
    }

    ctx.font      = isSelf ? 'bold 12px Segoe UI, sans-serif' : '11px Segoe UI, sans-serif';
    ctx.fillStyle = color;
    ctx.textAlign = 'center';
    const txtY = cy - size - 8;
    ctx.fillText(label, cx, txtY < pad ? cy + size + 14 : txtY);
  }

  state.competitors.forEach(c => plotDot(c.x, c.y, c.color, c.name, 9, false));

  const myX    = parseFloat(document.getElementById('my-x').value)    || 60;
  const myY    = parseFloat(document.getElementById('my-y').value)    || 75;
  const myName = document.getElementById('my-name').value             || 'Moi';
  plotDot(myX, myY, '#2563eb', myName, 11, true);
}

function renderPositioningAnalysis() {
  const myX         = parseFloat(document.getElementById('my-x').value) || 60;
  const myY         = parseFloat(document.getElementById('my-y').value) || 75;
  const competitors = state.competitors;

  if (competitors.length === 0) {
    document.getElementById('positioning-analysis').innerHTML =
      '<p style="color:var(--muted)">Ajoutez des concurrents pour obtenir une analyse.</p>';
    return;
  }

  const distances = competitors.map(c => ({
    name:  c.name,
    dist:  Math.sqrt(Math.pow(myX - c.x, 2) + Math.pow(myY - c.y, 2)).toFixed(1),
    xDiff: (myX - c.x).toFixed(1),
    yDiff: (myY - c.y).toFixed(1),
  })).sort((a, b) => a.dist - b.dist);

  const closest = distances[0];
  const xLabel  = myX > 50 ? document.getElementById('axis-x-right').value : document.getElementById('axis-x-left').value;
  const yLabel  = myY > 50 ? document.getElementById('axis-y-top').value   : document.getElementById('axis-y-bottom').value;

  const rows = distances.map(d => `
    <tr>
      <td>${d.name}</td>
      <td>${d.dist} pts</td>
      <td>${parseFloat(d.xDiff) > 0 ? '+'+d.xDiff+' →' : d.xDiff+' ←'} X</td>
      <td>${parseFloat(d.yDiff) > 0 ? '+'+d.yDiff+' ↑' : d.yDiff+' ↓'} Y</td>
    </tr>`).join('');

  document.getElementById('positioning-analysis').innerHTML = `
    <div class="alert alert-info" style="margin-bottom:14px;">
      📍 Votre entreprise se positionne en <strong>"${xLabel} / ${yLabel}"</strong>.
      Le concurrent le plus proche est <strong>${closest.name}</strong> (distance : ${closest.dist} pts).
    </div>
    <table class="segments-table">
      <thead><tr><th>Concurrent</th><th>Distance</th><th>Écart X</th><th>Écart Y</th></tr></thead>
      <tbody>${rows}</tbody>
    </table>
  `;
}

function addCompetitor() {
  const colors = ['#ef4444', '#f97316', '#8b5cf6', '#06b6d4', '#10b981', '#f59e0b', '#ec4899'];
  state.competitors.push({
    name:  `Concurrent ${state.competitors.length + 1}`,
    x:     Math.round(20 + Math.random() * 60),
    y:     Math.round(20 + Math.random() * 60),
    color: colors[state.competitors.length % colors.length],
  });
  renderPositioning();
}

function removeCompetitor(i) {
  state.competitors.splice(i, 1);
  renderPositioning();
}
