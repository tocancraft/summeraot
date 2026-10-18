#!/usr/bin/env python3
"""Checks selected binary API signatures; does not compile or launch Minecraft."""
from pathlib import Path
import subprocess

root = Path(__file__).resolve().parents[1]
jar = root / 'libs/dannys-aot-2.5.0.jar'
if not jar.exists():
    raise SystemExit(f'Missing DAOT jar: {jar}')

def inspect(name, required):
    run = subprocess.run(['javap', '-p', '-classpath', str(jar), name],
                         capture_output=True, text=True, check=True)
    for signature in required:
        if signature not in run.stdout:
            raise RuntimeError(f'Missing: {name} :: {signature}')
    print(f'OK: {name}, {len(required)} signatures')

inspect('daot.AttackTitanEntity', ['spawnHitboxes()', 'triggerAbility(int)',
       'napeEntity;', 'eyeEntity;', 'grabHitboxEntity;', 'onPlayerShift('])
inspect('daot.AttackTitanNapeEntity', ['setParentTitan(', 'getClientInstance(', 'setRendererPosition('])
inspect('daot.AttackTitanEyeEntity', ['setParentTitan(', 'getClientInstance(', 'setRendererPosition('])
inspect('daot.AttackTitanGrabEntity', ['setParentTitan(', 'getClientInstance(', 'setRendererPosition('])
inspect('daot.TitanBoneCache', ['getAnimatedBoneWorldPos('])
inspect('daot.network.ModNetworking', ['spawnAttackTitan('])
print('Binary signatures found. Compile and runtime behavior remain unverified.')
