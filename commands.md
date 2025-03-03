# Commands

All commands are accessible using `/adaptiveview`.

## Notifications

`/adaptiveview notifications` allows you to `subscribe` and `unsubscribe` to notifications when the view distance `changes` or a `lock` is set or removed.

## Lock

`/adaptiveview lock` allows you to check if the View or Simulation Distance is currently locked and allows to lock it.

- `main [chunks] {[condition]}` locks the View Distance and Simulation Distance to `chunks`.
- `view [chunks] {[condition]}` locks the View Distance to `chunks`.
- `simulation [chunks] {[condition]}` locks the Simulation Distance to `chunks`.
- `chunk-tick [chunks] {[condition]}` locks the Chunk-Ticking Distance to `chunks`.

#### Conditions
`conditon` is a optional condition that allows you to specify when the View Distance lock is cleared. There are the following conditions:

  - `player [name] disconnect` the specified player leaves the game
  - `player [name] move` the specified player moves
  - `timeout [ticks]` the specified time in ticks has passed

## Unlock

`/adaptiveview unlock` allows you to unlock the View and Simulation Distance.

- `all` clears all locks, that don't have a condition for automatic unlocking
- `main` clears all View Distance and Simulation Distance locks, that don't have a condition for automatic unlocking
- `view` clears all View Distance locks, that don't have a condition for automatic unlocking
- `simulation` clears all Simulation Distance locks, that don't have a condition for automatic unlocking
- `chunk-tick` clears all Chunk-Ticking Distance locks, that don't have a condition for automatic unlocking

- `[unlock] clear` also clears the locks, that have a condition for automatic unlocking

## Config

`/adaptiveview config` allows you to view and set all config options

### General usage

- `[option-name]` allows you to view the option
- `[option-name] [value]` sets the value of the option
- `[option-name] clear` clears the value of the option (only available for some options)

### Rules
- `rules` shows you an index list of all options
- `rules [index]` selects a specific rule to view and edit
- `rules add [type] [condition-options]` adds a rule of the specific type with specified condition options (the action needs to be edited afterwards)

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
|   |   main +
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
|   |   chunk-tick +
|   |   |   [chunks: int]
|   |   |   |   timeout +
|   |   |   |   |   [ticks: int]
|   |   |   |   player +
|   |   |   |   |   [player: player] +
|   |   |   |   |   |   disconnect
|   |   |   |   |   |   move
|   unlock
|   |   all
|   |   |   clear
|   |   main
|   |   |   clear
|   |   view
|   |   |   clear
|   |   simulation
|   |   |   clear
|   |   chunk-tick
|   |   |   clear
|   config
|   |   status
|   |   reload
|   |   broadcast-changes
|   |   |   none
|   |   |   ops
|   |   |   all
|   |   broadcast-lock
|   |   |   none
|   |   |   ops
|   |   |   all
|   |   update-rate
|   |   |   [ticks: int]
|   |   max-view-distance
|   |   |   [chunks: int]
|   |   min-view-distance
|   |   |   [chunks: int]
|   |   max-simulation-distance
|   |   |   [chunks: int]
|   |   min-simulation-distance
|   |   |   [chunks: int]
|   |   max-chunk-tick-distance
|   |   |   [chunks: int]
|   |   min-chunk-tick-distance
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
|   |   |   |   |   |   chunk-tick
|   |   |   |   |   update-rate
|   |   |   |   |   |   [ticks: int]
|   |   |   |   |   |   clear
|   |   |   |   |   max-distance
|   |   |   |   |   |   [chunks: int]
|   |   |   |   |   |   clear
|   |   |   |   |   min-distance
|   |   |   |   |   |   [chunks: int]
|   |   |   |   |   |   clear
|   |   |   |   |   step
|   |   |   |   |   |   [step: int]
|   |   |   |   |   |   clear
|   |   |   |   |   step-after
|   |   |   |   |   |   [step-after: int]
|   |   |   |   |   |   clear
|   |   |   add +
|   |   |   |   mspt +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   |   view
|   |   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   |   chunk-tick
|   |   |   |   memory +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   |   view
|   |   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   |   chunk-tick
|   |   |   |   players +
|   |   |   |   |   min +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   max + 
|   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   range +
|   |   |   |   |   |   [min: int] +
|   |   |   |   |   |   |   [max: int] +
|   |   |   |   |   |   |   |   view
|   |   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   |   chunk-tick
|   |   |   |   |   names +
|   |   |   |   |   |   [names: string] +
|   |   |   |   |   |   |   view
|   |   |   |   |   |   |   simulation
|   |   |   |   |   |   |   chunk-tick
```
