#!/usr/bin/env python3
"""Instala novos arquivos no SummerAOT existente sem apagar a integração do Krugger."""
from pathlib import Path
import sys, json, re, shutil, datetime

SRC_ROOT = Path(__file__).resolve().parent


def fail(msg):
    print('ERRO:',msg,file=sys.stderr)
    raise SystemExit(2)


def run():
    if len(sys.argv) != 2:
        fail('Uso: py -3 install_grisha.py "C:\\Users\\...\\summeraot"')
    dest = Path(sys.argv[1]).expanduser().resolve()
    manifest = dest/'src/main/resources/fabric.mod.json'
    props = dest/'gradle.properties'
    if not manifest.is_file() or not props.is_file():
        fail(f'Projeto Fabric nao encontrado em {dest}')
    original = json.loads(manifest.read_text(encoding='utf-8-sig'))
    if original.get('id') != 'summeraot':
        fail(f"mod id inesperado: {original.get('id')}; esperado 'summeraot'")
    entries = original.setdefault('entrypoints',{})
    if not isinstance(entries,dict):
        fail('entrypoints nao eh objeto')

    for kind, class_name in [('main','dev.summeraot.grisha.GrishaBootstrap'),
                             ('client','dev.summeraot.grisha.client.GrishaClient')]:
        arr=entries.setdefault(kind,[])
        if not isinstance(arr,list):
            fail(f'entrypoints.{kind} nao eh array')
        if class_name not in arr:
            arr.append(class_name)
    new_manifest=json.dumps(original,indent=2,ensure_ascii=False)+'\n'
    orig_props=props.read_text(encoding='utf-8-sig')
    pat=r'(?m)^\s*mod_version\s*=\s*.*$'
    if not re.search(pat,orig_props):
        fail('gradle.properties sem mod_version')
    new_props=re.sub(pat,'mod_version=0.0.4-alpha.1',orig_props,count=1)

    source_files = [p for p in (SRC_ROOT/'src').rglob('*') if p.is_file()]
    existing = [p.relative_to(SRC_ROOT) for p in source_files if (dest/p.relative_to(SRC_ROOT)).exists() and not (p.name in ('pt_br.json','en_us.json') and '/lang/' in p.relative_to(SRC_ROOT).as_posix())]
    if existing:
        fail('Arquivos ja existem (nenhum foi alterado): '+', '.join(str(x) for x in existing[:10]))

    # Se a pasta de lang ja existir, combinar apenas as novas chaves sem apagar idiomas atuais.
    writes=[]
    for p in source_files:
        rel=p.relative_to(SRC_ROOT)
        target=dest/rel
        if p.name in ('pt_br.json','en_us.json') and '/lang/' in rel.as_posix() and target.exists():
            old=json.loads(target.read_text(encoding='utf-8-sig'))
            new=json.loads(p.read_text(encoding='utf-8'))
            for key,value in new.items():
                if key in old and old[key]!=value:
                    fail(f'Conflito de idioma: {key}')
            old.update(new)
            writes.append((target,json.dumps(old,ensure_ascii=False,indent=2)+'\n'))
        else:
            writes.append((target,p.read_bytes()))

    # Os arquivos fonte nao sao substituidos, e os arquivos de configuracao fazem backup.
    stamp=datetime.datetime.now().strftime('%Y%m%d_%H%M%S')
    backup=dest/f'grisha_0_0_4_backup_{stamp}'
    backup.mkdir(parents=False,exist_ok=False)
    shutil.copy2(manifest, backup/'fabric.mod.json')
    shutil.copy2(props, backup/'gradle.properties')
    for path,payload in writes:
        path.parent.mkdir(parents=True,exist_ok=True)
        if isinstance(payload,bytes):
            path.write_bytes(payload)
        else:
            path.write_text(payload,encoding='utf-8')
    manifest.write_text(new_manifest,encoding='utf-8')
    props.write_text(new_props,encoding='utf-8')
    print(f'OK: {len(writes)} arquivos copiados, registradores/entrypoints adicionados.')
    print(f'Backup de metadados: {backup}')
    print('Compile com: .\\gradlew.bat --no-daemon clean build')
    print('Selecao/persistencia OmniShifter Grisha AINDA NAO foi conectada.')

if __name__=='__main__':
    run()
