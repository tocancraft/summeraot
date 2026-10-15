# SummerAOT 0.0.3 Alpha 2 — Compatibilidade OmniShifter

## Diagnóstico observado no OmniShifter 1.3.0

- `OmniShifterClient.availablePowers`: lista `List<TitanPowerType>` nativa, incapaz de conter Krugger.
- `PlayerTitanPowersState`: serializa somente ordinais de `TitanPowerType`.
- O botão da Alpha 1 não conhecia a posse real do Krugger no cliente.

## Integração implementada

1. O cliente recebe `summeraot:krugger_state` do servidor, contendo posse, seleção e cooldown.
2. Um cartão renderizado na tela do OmniShifter usa o sprite `assets/omnishifter/textures/gui/titan_icons/krugger.png`.
3. Clicar no cartão envia `summeraot:select_krugger`. O servidor valida posse, estado da transformação e cooldown.
4. A seleção do Krugger preserva os poderes nativos do Omni. Não escreve ordinais inválidos no estado do Omni.
5. Ao selecionar outro titã **com sucesso**, o Mixin na chamada a `TitanPowerTracker.syncPlayer` no receptor de seleção nativa do Omni agenda a retirada da seleção Krugger. Tentativas recusadas não retiram a seleção.
6. Os comandos `/daot shifter set/remove ... krugger` sincronizam imediatamente o estado.

### Limites e riscos

- Não é possível adicionar Krugger ao enum `TitanPowerType` sem alterar o DAOT; por isso ele ocupa um cartão adicional dentro da tela do Omni.
- Requer **OmniShifter 1.3.0 incorporado**, tal como a Alpha 1; não instale outro JAR Omni externo.
- A Alpha 2 NÃO foi compilada nesta sessão. Valide `clean build` e inicialização; em caso de falha no Mixin `OmniNativeSelectMixin`, enviar `latest.log`.
- O cooldown foi alinhado com a rotina `SelectPowerC2SPayload` do OmniShifter 1.3.0 (30 s, exceto modo criativo).
- O DAOT permanece uma dependência externa; não é modificado. A posse de `krugger` permanece independente.

### Teste recomendado

1. `/daot shifter set @s krugger`; abrir Omni (P): ver cartão com sprite Krugger.
2. Fechar e reabrir menu, selecionar Krugger, esperar o estado `ATIVO`.
3. Com Attack original disponível, selecionar Attack e confirmar que o cartão perde `ATIVO`.
4. Selecionar Krugger outra vez após 30 s; transformar pela tecla nativa; confirmar o modelo e as habilidades sem endurecimento/Berserk.
5. Remover poder Krugger pelo `/daot` e reabrir Omni: o cartão não deve mais ser exibido.
6. Sair e entrar no mundo: verificar persistência de posse e seleção.
