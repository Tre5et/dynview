# Configuration

The configuration is stored in `config/adaptiveview.json`. A default config is generated when the server is first started with the mod. Otherwise you can find the default configuration [here](default_config.json).

## Basic Configuration

### Global rules

These values apply if there are no rules active that override them.

- `update_rate`: The interval in ticks after which performance is checked and view distance is adjusted.
- `max_view_distance`: The maximum view distance the dynamic adjustment can go up to.
- `min_view_distance`: The minimum view distance the dynamic adjustment can go down to.

### Other Configurations
- `allow_on_client`: If this is enabled the mod will try to work in a client only environment. This can lead to unexpected behaviour.
- `broadcast_to_ops`: If enabled, all operators get notified whenever the mod changes view distance.
- `broadcast_to`: List of (case-insensitive) player names that get notification whenever the mod changes the view distance.

## Rules

Rules specify by which conditions view distance is adjusted. They are made up of a condition and an action that is applied when the condition is met.

Rules also support custom names using the `name` property.

### Condition
- `type`: One of `MSPT`, `MEMORY` or `PLAYERS`. The kind of condition this is.

All the following need to be met for the rule to be active:

- `min`: The minimum MSPT, Memory usage (in percent) or online Players required for the rule to be active.
- `max`: The maximum MSPT, Memory usage (in percent) or online players allowed for the rule to be active.
- `value`: A list of (case-insensitive) player names (only active if `type` is `PLAYERS`) separated by `,`. The string can be prefixed with the following values to specify when the rule is active:
  - No prefix: Any of the specified players are online
  - `&`: All of the specified players are online
  - `!`: Any not specified player is online
  - `\`: None of the specified players are online

### Action

If the condition is met, the following actions are performed:

- `update_rate`: Overrides the global `update_rate` value.
- `max_view_distance`: Overrides the global `max_view_distance` value.
- `min_view_distance`: Overrides the global `min_view_distance` value.
- `step`: The amount of chunks the view distance is changed by every update. Positive values increase the view distance and negative values decrease it. 
- `step_after`: The amount of update after which the step is performed. E.g. if `update_rate` is `100` and `step_after` is `2`, the `step` will be performed every `200` ticks.

### Resolving conflicts

If the same value is set by multiple active rules, the following way of resolving conflicts is used:

- `update_rate`: The lowest value is used.
- `min_view_distance`: The highest value is used.
- `max_view_distance`: The lowest value is used. If `min_view_distance` is greater than `max_view_distance`, the value of `min_view_distance` is used.
- `step`: If there are negative step values, the lowest one is used, else the highest one is used.
- `step_after`: Individual per rule. Effects the rule's `step`.

In general, the value that is more limiting is used.

## Examples

### The default config

A basic config applicable to most servers struggling for MSPT performance.

1. The View Distance is updated every 600 ticks (30 seconds).
2. The View Distance is allowed to be between 4 and 20 chunks.
3. If the server is between 40 and 50 MSPT, nothing happens.
4. If the server is above 50 MSPT, the view distance is adjusted down by 1 chunk every update. 
5. If the server is above 60 MSPT, the view distance is adjusted down by 2 chunks every update.
6. If the server is below 40 MSPT, the view distance is adjusted up by 1 chunk every update.
7. If the server is below 30 MSPT, the view distance is adjusted up by 2 chunks every update.



```json
{
  "update_rate": 600, // 1.
  "max_view_distance": 20, // 2.
  "min_view_distance": 4, // 2.
  [...]
  "rules": [
    {
      "type": "MSPT",
      "min": 60,
      "step": -2  // 5.
    },
    {
      "type": "MSPT",
      "min": 50,
      "step": -1  // 4.
    },
    {
      "type": "MSPT",
      "max": 40,
      "step": 1  // 6.
    },
    {
      "type": "MSPT",
      "max": 30,
      "step": 2  // 7.
    }
  ]
}
```

### Memory focused config

A config that could be used, if a server is low on memory.

> **Note**: Updating view distance based on memory too quickly can be problematic, as it can take a while for memory usage to change after decreasing view distance.

1. The View Distance is updated every 1200 ticks (1 mínute),
2. If there is at most one player online, the View Distance is at most 28 chunks.
3. If there are 2 players online, the View Distance is at most 15 chunks. The update rate is also increased to 300 ticks to allow for closer monitoring of memory usage.
4. If there are 3 or more players online, the View Distance is at most 12 chunks. The update rate of 300 ticks is still active from 3.
5. If the memory usage is at least 90%, the view distance is adjusted down by 1 every 2 minutes (2400 ticks).

```json
{
  "update_rate": 1200, // 1.
  "max_view_distance": 18, // 2.
  "min_view_distance": 4,
  [...]
  "rules": [
    {
      "type": "PLAYERS",
      "min": 2,
      "max_view_distance": 15, // 3.
      "update_rate": 300  // 3.
    },
    {
      "type": "PLAYERS",
      "min": 3,
      "max_view_distance": 12, // 4.
    },
    {
      "type": "MEMORY",
      "min": 90,
      "step": -1,  // 5.
      "step_after": 8,  // 5.
      "update_rate": 300
    }
  ]
}
```

### Config that locks the view distance if specific players are online

This config may e.g. be useful if you have specific Alt-Accounts that are always used for running farms. This also works if the player is a carpet bot.

1. Everything works just like the default config.
2. If the player `FarmPlayer1` or `FarmPlayer2` is online, the view distance is locked to 6 chunks and the update rate is decreased to 5 Minutes, as nothing is changing.

```json
{
  "update_rate": 600,
  "max_view_distance": 20,
  "min_view_distance": 4,
  [...]
  "rules": [  
    {
      "type": "PLAYERS",
      "value": "farmplayer1,farmplayer2",
      "max_view_distance": 6,  // 2.
      "min_view_distance": 6,  // 2.
      "update_rate": 6000  // 2.
    },
    {
      "type": "MSPT",
      "min": 60,
      "step": -2
    },
    {
      "type": "MSPT",
      "min": 50,
      "step": -1
    },
    {
      "type": "MSPT",
      "max": 40,
      "step": 1
    },
    {
      "type": "MSPT",
      "max": 30,
      "step": 2
    }
  ]
}
```

## Configuration Specification

Option names followed by a `?` denote optional options.

```json
{
  "name": string,
  "update_rate": int[1..],
  "max_view_distance": int[2..32],
  "min_view_distance": int[2..32],
  "allow_on_clinet": boolean,
  "broadcast_to_ops": boolean,
  "broadcast_to": [
    string
  ],
  "rules": [
    {
      "type?": string[MSPT/MEMORY/PLAYERS],
      "value?": string,
      "max?": int[0..],
      "max?": int[0..],
      "update_rate?": int[1..],
      "max_view_distance?": int[2..32],
      "min_view_distance?": int[2..32],
      "step?": int,
      "step_after?": int[1..]
    }
  ]
}
```
