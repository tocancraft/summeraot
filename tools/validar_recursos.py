#!/usr/bin/env python3
"""Static asset validation using Python's standard library only."""
from pathlib import Path
import json
import struct

root = Path(__file__).resolve().parents[1]
a = root / 'src/main/resources/assets/summeraot'
model = json.loads((a / 'geo/attack3titan.geo.json').read_text(encoding='utf8'))
animations = json.loads((a / 'animations/attack3titan.animation.json').read_text(encoding='utf8'))
geo = model['minecraft:geometry'][0]
bones = {b['name'] for b in geo['bones']}
clips = animations['animations']
assert len(bones) == 21, f'bones={len(bones)}'
assert len(clips) == 37, f'clips={len(clips)}'
for b in ('nape_hitbox', 'eye_hitbox', 'grab_hitbox'):
    assert b in bones, f'missing bone: {b}'
for filename in ('attack3titan.png', 'attack3titan2.png'):
    data = (a / 'textures/entity' / filename).read_bytes()
    assert data[:8] == b'\x89PNG\r\n\x1a\n'
    assert struct.unpack('>II', data[16:24]) == (1024, 1024), filename
for p in a.rglob('*.json'):
    json.loads(p.read_text(encoding='utf8'))
print(f'OK: {len(bones)} bones, {len(clips)} animations, two 1024x1024 PNGs, JSON valid')
