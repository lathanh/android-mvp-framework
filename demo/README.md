"Presenter" framework demo
-------------------------------------------------------------------------------
For now, just a couple quick hints:

  * The list is composed of two types of items, "Green" and "Red", each powered
    by their own Presenter
  * Each has an OnLongClickListener that causes the data model to be updated
    with the current time, requiring that the View Model (and then the View) be
    updated.

And a note:

  * The `ViewListener` (which implements the
    `CompoundButton.OnCheckedChangeListener` and `View.OnLongClickListener` for
    each row belongs to the ViewHolder.
    This results in ViewListeners only being instantiated once per recyclable
    view rather than one per item or upon each binding.
    This means that the ViewListener is updated with the current ViewModel upon
    each binding so that the listener knows what data to update.
