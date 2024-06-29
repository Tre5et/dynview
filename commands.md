# Commands

All commands are accessible using `/adaptiveview`.

## Notifications

`/adaptiveview notifications` allows you to `subscribe` and `unsubscribe` to notifications when the view distance `changes` or a `lock` is set or removed.

## Lock

`/adaptiveview lock` allows you to check if the View Distance is currently locked and allows to lock and unlock it.

### Locking
- `all [chunks] {[condition]}` locks the View Distance and Simulation Distance to `chunks`.
- `view [chunks] {[condition]}` locks the View Distance to `chunks`.
- `simulation [chunks] {[condition]}` locks the Simulation Distance to `chunks`.

#### Conditions
`conditon` is a optional condition that allows you to specify when the View Distance lock is cleared. There are the following conditions:

  - `player [name] disconnect` the specified player leaves the game
  - `player [name] move` the specified player moves
  - `timeout [ticks]` the specified time in ticks has passed

### Unlocking

- `unlock all` clears all lock, that don't have a condition for automatic unlocking
- `unlock view` clears all View Distance locks, that don't have a condition for automatic unlocking
- `unlock simulation` clears all Simulation Distance locks, that don't have a condition for automatic unlocking

- `[unlock] clear` also clears the locks, that have a condition for automatic unlocking

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
|   |   changes
|   |   |   subscribe
|   |   |   unsubscribe
|   |   lock
|   |   |   subscribe
|   |   |   unsubscribe
|   lock
|   |   status
|   |   all +
|   |   |   [chunks: int]
|   |   |   |   timeout +
|   |   |   |   |   [ticks: int]
|   |   |   |   player +
|   |   |   |   |   [player: player] +
|   |   |   |   |   |   disconnect
|   |   |   |   |   |   move
|   |   view +
|   |   |   [chunks: int]
|   |   |   |   timeout +
|   |   |   |   |   [ticks: int]
|   |   |   |   player +
|   |   |   |   |   [player: player] +
|   |   |   |   |   |   disconnect
|   |   |   |   |   |   move
|   |   simulation +
|   |   |   [chunks: int]
|   |   |   |   timeout +
|   |   |   |   |   [ticks: int]
|   |   |   |   player +
|   |   |   |   |   [player: player] +
|   |   |   |   |   |   disconnect
|   |   |   |   |   |   move
|   |   unlock
|   |   |   all
|   |   |   |   clear
|   |   |   view
|   |   |   |   clear
|   |   |   simulation
|   |   |   |   clear
|   config
|   |   status
|   |   reload
|   |   broadcast_changes
|   |   |   none
|   |   |   ops
|   |   |   all
|   |   broadcast_lock
|   |   |   none
|   |   |   ops
|   |   |   all
|   |   update_rate
|   |   |   [ticks: int]
|   |   max_view_distance
|   |   |   [chunks: int]
|   |   min_view_distance
|   |   |   [chunks: int]
|   |   rules
|   |   |   [index: int]
|   |   |   |   remove
|   |   |   |   name
|   |   |   |   |   [name: string]
|   |   |   |   |   clear
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
|   |   |   |   |   target
|   |   |   |   |   |   view
|   |   |   |   |   |   simulation
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
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   |   view
|   |   |   |   |   |   |   |   simulation
|   |   |   |   memory +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   |   view
|   |   |   |   |   |   |   |   simulation
|   |   |   |   players +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   |   view
|   |   |   |   |   |   |   |   simulation
|   |   |   |   |   names +
|   |   |   |   |   |   [names: string] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
```
