#!/usr/bin/env python3
"""Validação estática (não substitui compilar nem testar dentro do jogo)."""
import json
from pathlib import Path
r = Path(__file__).resolve().parents[1]
code = (r / 'src/main/java/dev/summeraot/KruggerAbilityPolicy.java').read_text('utf-8')
entity = (r / 'src/main/java/dev/summeraot/entity/KrugerTitanEntity.java').read_text('utf-8')
assert 'BERSERK_ABILITY_ID = 9' in code
assert 'HARDEN_ABILITY_ID = 6' in code
assert 'isAllowedAbility(abilityId)' in entity
assert 'setHardeningMode(0)' in entity
assert 'setTitanClimbing(false)' in entity
assert 'summeraot.power.krugger' in (r/'src/main/java/dev/summeraot/SummerAOTPower.java').read_text('utf-8')
assert not (r/'src/main/java/dev/summeraot/mixin/AttackTitanSpawnMixin.java').exists()
json.loads((r/'src/main/resources/fabric.mod.json').read_text('utf-8'))
print('OK: Krugger independente; IDs 6 e 9 bloqueados; endurecimento desativado.')
