function currency() {
  return document.getElementById('currency')?.value ?? '€';
}

function fmt(n) {
  return n.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' ' + currency();
}

function getInputs() {
  const varCost   = parseFloat(document.getElementById('var-cost').value)   || 0;
  const fixedCost = parseFloat(document.getElementById('fixed-cost').value) || 0;
  const volume    = parseFloat(document.getElementById('volume').value)     || 1;
  const margin    = parseFloat(document.getElementById('margin').value)     || 0;
  return { varCost, fixedCost, volume, margin };
}

function calcCosts() {
  const { varCost, fixedCost, volume, margin } = getInputs();
  const costPerUnit  = varCost + fixedCost / volume;
  const minPrice     = costPerUnit;
  // PRD §4.1: optimalPrice = costPerUnit / (1 − margin/100)
  // This yields the price where effective margin equals the target margin.
  const optimalPrice = margin >= 100 ? Infinity : costPerUnit / (1 - margin / 100);
  return { varCost, fixedCost, volume, margin, costPerUnit, minPrice, optimalPrice };
}

function strategyPrice(optimalPrice) {
  switch (state.strategy) {
    case 'luxury':        return optimalPrice * 2.2;
    case 'penetration':   return optimalPrice * 1.05;
    case 'alignment':     return state.alignmentPrice != null ? state.alignmentPrice : optimalPrice;
    case 'discriminatory':return optimalPrice;
    default:              return optimalPrice;
  }
}

function applyPriceType(price) {
  switch (state.pricetype) {
    case 'magic': {
      const base = Math.floor(price);
      return base > 0 ? base - 0.01 : 0.99;
    }
    case 'psychological': {
      const thresholds = [1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000];
      let threshold = thresholds.find(t => t > price);
      if (!threshold) threshold = Math.ceil(price / 1000) * 1000;
      return threshold - 3;
    }
    case 'rounded': {
      if (price <= 0) return 0;
      const mag  = Math.pow(10, Math.floor(Math.log10(price)));
      const step = mag >= 1000 ? 500 : mag >= 100 ? 50 : mag >= 10 ? 5 : 1;
      return Math.round(price / step) * step;
    }
    default: return price;
  }
}

function finalPrice() {
  const { optimalPrice } = calcCosts();
  return applyPriceType(strategyPrice(optimalPrice));
}
