# Команды и права iCommands

Колонка «По умолчанию»: **все** — право есть у каждого игрока, **операторы** — только у операторов,
**никто** — право нужно выдать через LuckPerms.

Если в `config.yml` для команды стоит `settings.requirePermission.<команда>: false`, команда на себя доступна
всем. Права на других игроков (`.others`) и остальные нужны всегда.

## Команды

| Команда | Синонимы | Что делает | Право |
|---|---|---|---|
| `/feed [игрок]` | — | Утолить голод | `icommands.feed`, на другого — `icommands.feed.others` |
| `/heal [игрок]` | — | Восстановить здоровье | `icommands.heal`, на другого — `icommands.heal.others` |
| `/fly [игрок]` | — | Включить или выключить полёт | `icommands.fly`, на другого — `icommands.fly.others` |
| `/speed [fly\|walk] <0-10> [игрок]` | — | Скорость полёта или ходьбы | `icommands.speed`, на другого — `icommands.speed.others` |
| `/god [игрок]` | — | Бессмертие | `icommands.god`, на другого — `icommands.god.others` |
| `/ext [игрок]` | — | Потушить огонь | `icommands.ext`, на другого — `icommands.ext.others` |
| `/vanish [игрок]` | `/v` | Невидимость | `icommands.vanish`, на другого — `icommands.vanish.others` |
| `/nick [игрок] <ник\|off>` | — | Сменить ник или вернуть свой | `icommands.nick`, на другого — `icommands.nick.others` |
| `/tpa <игрок>` | — | Попросить телепортироваться к игроку | `icommands.tpa` |
| `/tpahere <игрок>` | — | Позвать игрока к себе | `icommands.tpahere` |
| `/tpaccept [игрок]` | — | Принять запрос, без ника — последний | `icommands.tpaccept` |
| `/tpdeny [игрок]` | — | Отклонить запрос, без ника — последний | `icommands.tpdeny` |
| `/tpacancel [игрок]` | — | Отозвать свои запросы, с ником — только к нему | `icommands.tpacancel` |
| `/tptoggle [игрок]` | — | Не принимать запросы или принимать снова | `icommands.tptoggle`, на другого — `icommands.tptoggle.others` |

Запрос ждёт ответа 60 секунд, у игрока не больше 5 запросов: новый вытесняет самый старый. Запросы только в
памяти и только на этом сервере. Задержку перед телепортом ждёт тот, кто перемещается; движение или урон
отменяют телепорт.

## Права

| Право | Что даёт | По умолчанию |
|---|---|---|
| `icommands.feed` | `/feed` | операторы |
| `icommands.feed.others` | `/feed <игрок>` | операторы |
| `icommands.heal` | `/heal` | операторы |
| `icommands.heal.others` | `/heal <игрок>` | операторы |
| `icommands.fly` | `/fly` | операторы |
| `icommands.fly.others` | `/fly <игрок>` | операторы |
| `icommands.speed` | `/speed` | операторы |
| `icommands.speed.others` | `/speed ... <игрок>` | операторы |
| `icommands.god` | `/god` | операторы |
| `icommands.god.others` | `/god <игрок>` | операторы |
| `icommands.god.pvp` | Бить других игроков в бессмертии | операторы |
| `icommands.ext` | `/ext` | операторы |
| `icommands.ext.others` | `/ext <игрок>` | операторы |
| `icommands.vanish` | `/vanish` | операторы |
| `icommands.vanish.others` | `/vanish <игрок>` | операторы |
| `icommands.vanish.see` | Видеть невидимых игроков | операторы |
| `icommands.vanish.pvp` | Бить других игроков в невидимости | операторы |
| `icommands.vanish.pickup` | Подбирать предметы в невидимости | операторы |
| `icommands.nick` | `/nick` | операторы |
| `icommands.nick.others` | `/nick <игрок> ...` | операторы |
| `icommands.nick.color` | Цвета в нике | операторы |
| `icommands.tpa` | `/tpa` | операторы |
| `icommands.tpahere` | `/tpahere` | операторы |
| `icommands.tpaccept` | `/tpaccept`; без него игроку нельзя отправить запрос | операторы |
| `icommands.tpdeny` | `/tpdeny` | операторы |
| `icommands.tpacancel` | `/tpacancel` | операторы |
| `icommands.tptoggle` | `/tptoggle` | операторы |
| `icommands.tptoggle.others` | `/tptoggle <игрок>` | операторы |
| `icommands.tpa.delay.<секунды>` | Задержка перед телепортом по запросу. Если таких прав несколько, берётся наименьшее, `0` — сразу. Без права — `settings.teleportDelay` | никто |
| `icommands.tpa.bypass.delay` | Телепорт по запросу без задержки, что бы ни давали права `icommands.tpa.delay.<секунды>` | операторы |
