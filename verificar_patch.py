from pathlib import Path
import json, hashlib
root = Path(__file__).resolve().parent
assets = root/'src/main/resources/assets/summeraot'
path_geo = assets/'geo/attack4titan.geo.json'
path_anim = assets/'animations/attack4titan.animation.json'
geo = json.loads(path_geo.read_text(encoding='utf-8'))
anim = json.loads(path_anim.read_text(encoding='utf-8'))
bones=geo['minecraft:geometry'][0]['bones']
names=list(anim['animations'])
assert len(bones)==25, len(bones)
assert len(names)==37, len(names)
assert all((assets/'textures/entity'/p).is_file() for p in ['attack4titan.png','attack4titan2.png'])
java = (root/'src/main/java/dev/summeraot/grisha/entity/GrishaTitanEntity.java').read_text()
assert 'ABILITY_HARDENING = 6' in java
assert 'super.setHardeningMode(0)' in java
assert 'super.setTitanClimbing(false)' in java
assert 'triggerAbility(int ability)' in java
policy=(root/'src/main/java/dev/summeraot/grisha/GrishaPowerIds.java').read_text()
assert 'ALLOW_BERSERKER = false' in policy
assert 'ALLOW_HARDENING = false' in policy
print('OK — Grisha: 25 ossos, 37 animacoes, texturas e bloqueios presentes')
for file in sorted(assets.rglob('*')):
    if file.is_file():
        print(hashlib.sha256(file.read_bytes()).hexdigest(), file.relative_to(root))
