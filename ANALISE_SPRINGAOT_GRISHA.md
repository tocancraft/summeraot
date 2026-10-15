# Levantamento do Grisha — SpringAOT 3.5.2

Extraido do pacote `springaot_9_formas_referencia_completa.zip`, que preserva os arquivos do `springdaot-3.5.2.rar` anteriormente fornecido.

- `Attack4TitanEntity` estende `daot.AttackTitanEntity` (classe de ataque base).
- `Attack4TitanModel` aponta para `springdaot:geo/attack4titan.geo.json`, `springdaot:textures/entity/attack4titan.png` e `springdaot:animations/attack4titan.animation.json`.
- `Attack4TitanRenderer` sincroniza tres hitboxes por ossos; a implementacao simplificada incluida requer revisao de posicionamento avancado.
- `Attack4TitanNapeEntity`, `Attack4TitanEyeEntity`, `Attack4TitanGrabEntity` sao independentes no SpringAOT.
- A geometria `attack4titan` tem 25 bones e atlas declarado 512×512.
- As animacoes `attack4titan` tem os mesmos nomes (37) das de `attack3titan` (Krugger); os keyframes nao sao necessariamente identicos.
- `attack4titan2.png` e opcional sem uso direto identificado em `Attack4TitanModel`.
- SpringAOT 3.5.2 contem `attack_4_syringe.png` e respectiva definicao de modelo.

## Limites da primeira etapa

- Recursos incorporados: geometria, as 37 animacoes e as duas texturas (original + alternativa).
- Entidade, hitboxes e renderer independentes preparados em codigo-fonte Java.
- Falta a integracao com o estado de poder e o OmniShifter Alpha 7, cujos fontes nao estavam neste contexto.
- Nao houve compilacao/teste funcional contra o SummerAOT Alpha 7; nao tratar este pacote como release validada.
