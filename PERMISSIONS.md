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
