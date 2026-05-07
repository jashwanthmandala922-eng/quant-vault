import json
from networkx.readwrite import json_graph

G = json_graph.node_link_graph(json.load(open('graphify-out/graph.json')), edges='links')

terms = ['argon', 'shamir', 'opa', 'pqc', 'postquant', 'mlkem', 'mldsa', 'x25519', 'nonc', 'secure', 'memory', 'wipe']
found = {t: 0 for t in terms}

for n, d in G.nodes(data=True):
    l = d.get('label', '').lower()
    for t in terms:
        found[t] += l.count(t)

print('=== SECURITY FEATURES FOUND ===')
for t, c in sorted(found.items(), key=lambda x: -x[1]):
    if c > 0:
        print(f'{t}: {c}')