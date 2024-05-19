# Commands

All commands are accessible using `/adaptiveview`.

## Notifications

`/adaptiveview notifications` allows you to check if you are receiving notifications and allows you to `subscribe` and `unsubscribe`.

## Lock

`/adaptiveview lock` allows you to check if the View Distance is currently locked and allows to lock and unlock it.

### Locking
`set [chunks] {[condition]}` allows you to lock the View Distance to the range specified in `chunks`.

- `conditon` is a optional condition that allows you to specify when the View Distance lock is cleared. There are the following conditions:

  - `player [name] disconnect` the specified player leaves the game
  - `player [name] move` the specified player moves
  - `timeout [ticks]` the specified time in ticks has passed

### Unlocking

- `unlock` clears all lock, that don't have a condition for automatic unlocking

- `unlock clear` clears all lock, also those that have a condition associated

## Config

`/adaptiveview config` allows you to view and set all config options

### General usage

- `[option_name]` allows you to view the option
- `[option_name] [value]` sets the value of the option
- `[option_name] clear` clears the value of the option (only available for some options)

### Rules
- `rules` shows you an index list of all options
- `rules [index]` selects a specific rule to view and edit
- `rules add [type] [condition_options]` adds a rule of the specific type with specified condition options (the action needs to be edited afterwards)

## All commands:

Commands followed by a `+` require one of the following options to work.

```
adaptiveview
|   status
|   notifications
|   |   subscribe
|   |   unsubscribe
|   lock
|   |   status
|   |   set +
|   |   |   [chunks: int]
|   |   |   |   timeout +
|   |   |   |   |   [ticks: int]
|   |   |   |   player +
|   |   |   |   |   [player: player] +
|   |   |   |   |   |   disconnect
|   |   |   |   |   |   move
|   |   unlock
|   |   |   clear
|   config
|   |   status
|   |   reload
|   |   update_rate
|   |   |   [ticks: int]
|   |   max_view_distance
|   |   |   [chunks: int]
|   |   min_view_distance
|   |   |   [chunks: int]
|   |   rules
|   |   |   [index: int]
|   |   |   |   remove
|   |   |   |   condition
|   |   |   |   |   type
|   |   |   |   |   |   mspt
|   |   |   |   |   |   memory
|   |   |   |   |   |   players
|   |   |   |   |   value
|   |   |   |   |   |   [value: string]
|   |   |   |   |   |   clear
|   |   |   |   |   min
|   |   |   |   |   |   [min: int]
|   |   |   |   |   |   clear
|   |   |   |   |   max
|   |   |   |   |   |   [max: int]
|   |   |   |   |   |   clear
|   |   |   |   action
|   |   |   |   |   update_rate
|   |   |   |   |   |   [ticks: int]
|   |   |   |   |   |   clear
|   |   |   |   |   max_view_distance
|   |   |   |   |   |   [chunks: int]
|   |   |   |   |   |   clear
|   |   |   |   |   min_view_distance
|   |   |   |   |   |   [chunks: int]
|   |   |   |   |   |   clear
|   |   |   |   |   step
|   |   |   |   |   |   [step: int]
|   |   |   |   |   |   clear
|   |   |   |   |   step_after
|   |   |   |   |   |   [step_after: int]
|   |   |   |   |   |   clear
|   |   |   add +
|   |   |   |   mspt +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int]
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int]
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int]
|   |   |   |   memory +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int]
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int]
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int]
|   |   |   |   players +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int]
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int]
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int]
|   |   |   |   |   names +
|   |   |   |   |   |   [names: string]
```
