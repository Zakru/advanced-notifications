# Advanced Notifications

An advanced notifications system for RuneLite

Allows users to set up many kinds of notifications which
cause the generic notification as defined in the RuneLite config (just
like, for example, the idle notifier).

Icons are placeholders right now.

## Notification types

This is a list of the types of notifications you can use

### Inventory

This will be fired whenever the count of the specified item changes,
fulfilling the condition:

* **+-** Any time you get or lose the item
* **+** Any time you get the item
* **-** Any time you lose the item
* **=** Any time the count becomes equal to the specified count
* **≠** Any time the count is no longer equal to the specified count
* **<** Any time the count becomes less than the specified count
* **>** Any time the count becomes greater than the specified count
* **≤** Any time the count becomes less than or equal to the specified
  count
* **≥** Any time the count becomes greater than or equal to the
  specified count

### Empty Space

Same as *Inventory*, but when the amount of empty space in your
inventory changes.

### Group

A group of notifications that can be turned on or off at once. Can also
be renamed. Add notifications by dragging them into it.

## Reordering

You can drag notifications by their title to reorder and organize them.

![Example](https://i.imgur.com/hgwMLUS.gif)

## Cloning

Notifications can be cloned by right-clicking their title and selecting "Clone".
