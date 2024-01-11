# CivSim

CivSim is a bukkit plugin that creates a living economy through the use of an agent simulation optimized by genetic algorithms

## Commands

Currently there is 1 command, `/civsim` or `/csim` or `/cim` with various subcommands

Start with `/civsim info` for more information.

## Plugin Dependencies

None for now

## Build

Run `./gradlew clean spotlessApply shadowjar`

## Updating to 1.19 - 1.20

* BoundaryNodeListener
  * onPlayerPlaceBoundaryMarker - Update the way we get the item in the hand with #getHand() (added in 1.19) and in 1.20 update to hide all entities.