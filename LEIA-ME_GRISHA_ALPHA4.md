SummerAOT 0.0.8-alpha.4

Correcao: substituicao canonica e definitiva dos assets do Grisha.

O que foi feito:
- o geo enviado por ultimo foi copiado para:
  src/main/resources/assets/summeraot/geo/attack4titan.geo.json
- a textura enviada por ultimo foi copiada para:
  src/main/resources/assets/summeraot/textures/entity/attack4titan.png
- a mesma textura tambem foi copiada para:
  src/main/resources/assets/summeraot/textures/entity/attack4titan2.png
  para impedir que qualquer caminho alternativo use a skin antiga.
- os arquivos grisha_bestial_* antigos foram removidos do mod.
- GrishaTitanModel.java voltou a apontar para os nomes canonicos attack4titan.*
- ajustes de combate do Grisha foram preservados.

Importante:
- remova JARs antigos do SummerAOT da pasta mods antes de testar.
- copie apenas o novo JAR compilado.
