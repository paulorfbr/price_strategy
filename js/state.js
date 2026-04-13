const state = {
  strategy: 'luxury',
  pricetype: 'magic',
  alignmentPrice: null,
  segments: [
    { name: 'Starter',    multiplier: 0.7 },
    { name: 'Pro',        multiplier: 1.0 },
    { name: 'Enterprise', multiplier: 1.6 },
  ],
  competitors: [
    { name: 'Concurrent A', x: 50, y: 55, color: '#ef4444' },
    { name: 'Concurrent B', x: 80, y: 85, color: '#f97316' },
    { name: 'Concurrent C', x: 25, y: 40, color: '#8b5cf6' },
  ],
  swot: {
    strengths:     ['Expertise technique', 'Support client réactif'],
    weaknesses:    ['Prix élevé', 'Notoriété faible'],
    opportunities: ['Marché en croissance', 'Nouveaux secteurs cibles'],
    threats:       ['Concurrence prix agressive', 'Nouveaux entrants'],
  },
  ansoffQuadrant: 'penetration',
  ansoffInitiatives: [
    { name: 'Offre SaaS Core',      quadrant: 'penetration', description: '' },
    { name: 'Module Analytics Pro', quadrant: 'product-dev', description: '' },
  ],
};
