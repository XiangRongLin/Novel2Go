package com.kaiserpudding.novel2go.util.multiSelect

import android.view.ActionMode
import android.view.Menu
import androidx.fragment.app.Fragment

/**
 * A Fragment handling the logic needed for a recycler view with clickable and selectable items.
 *
 * Implements the [MultiSelectAdapter.MultiSelectAdapterItemInteractionListener] and basic things
 * of [ActionMode] to display a bar at the top when something is selected.
 *
 * @param T The type of the items shown in the list.
 * @param A The type of the adapter. Must be subclass of [MultiSelectAdapter]
 */
abstract class MultiSelectFragment<T, A : MultiSelectAdapter<T>> : Fragment(),
    MultiSelectAdapter.MultiSelectAdapterItemInteractionListener {

    protected lateinit var adapter: A
    protected var actionMode: ActionMode? = null

    /**
     * [ActionMode.Callback] implementation which relies on subclasses passing an object
     * which inherits from [MultiSelectActionModeCallback].
     */
    protected abstract val actionModeCallback: MultiSelectActionModeCallback

    /**
     * Implements part of the [ActionMode.Callback] interface needed for multi select.
     */
    abstract inner class MultiSelectActionModeCallback : ActionMode.Callback {

        /**
         * The id of the menu shown in the [ActionMode].
         */
        protected abstract val actionMenuId: Int

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(actionMenuId, menu)
            mode.title = "${adapter.numberOfSelected} selected"
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter.clearSelectedThenNotify()
            actionMode = null
        }
    }

    override fun onStop() {
        super.onStop()
        actionMode?.finish()
        adapter.clearSelectedThenNotify()
    }

    protected abstract fun onListInteraction(position: Int)

    /**
     * Start/Stop/Continue action mode depending on if [MultiSelectAdapter.inSelectionMode]
     * and update [ActionMode.setTitle] if action mode is still active.
     */
    override fun onDataSetChanged() {
        if (adapter.inSelectionMode) {
            if (actionMode == null) actionMode = activity?.startActionMode(actionModeCallback)
            actionMode?.title = "${adapter.numberOfSelected} selected"
        } else {
            actionMode?.finish()
        }
    }

    override fun onMultiSelectAdapterInteraction(position: Int) {
        onListInteraction(position)
    }
}