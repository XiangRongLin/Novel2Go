package com.kaiserpudding.novel2go.util.multiSelect

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kaiserpudding.novel2go.util.multiSelect.MultiSelectAdapter.MultiSelectAdapterItemInteractionListener
import com.kaiserpudding.novel2go.util.setSafeOnClickListener

/**
 * An adapter handling the logic needed for a recycler view with clickable and selectable items.
 *
 * @param T They type of the items shown in the list
 * @property listener A listener implementing [MultiSelectAdapterItemInteractionListener]
 * handling onClick and onLongClick actions on the items
 */
abstract class MultiSelectAdapter<T>(
    private val listener: MultiSelectAdapterItemInteractionListener
) : RecyclerView.Adapter<MultiSelectAdapter<T>.MultiSelectViewHolder>() {

    private val selectedIdSet = mutableSetOf<Long>()
    val selectedIdArray: LongArray
        get() = selectedIdSet.toLongArray()
    val numberOfSelected: Int
        get() = selectedPosition.size
    val inSelectionMode: Boolean
        get() = numberOfSelected > 0
    private val selectedPositionSet = mutableSetOf<Int>()
    val selectedPosition : IntArray
        get() = selectedPositionSet.toIntArray()

    /**
     * The id of the layout that is representing a single item in the recycler view
     */
    protected abstract val viewHolderId: Int

    /**
     * The list of the items that are represented in the recycler view
     */
    lateinit var list: List<T>


    override fun onBindViewHolder(holder: MultiSelectViewHolder, position: Int) {
        //if item at position is selected, activate it and thus show different background color
        holder.itemView.isActivated = selectedIdSet.contains(getItemId(position))
    }

    /**
     * Remove/Add the id of the item at [position] from [selectedIdSet] depending on if its already in there.
     * Then notifies listeners of changes.
     *
     * @param position
     */
    fun toggleSelectedThenNotify(position: Int) {
        if (selectedPositionSet.contains(position)) selectedPositionSet.remove(position)
        else selectedPositionSet.add(position)

        val id = getItemId(position)
        if (selectedIdSet.contains(id)) selectedIdSet.remove(id)
        else selectedIdSet.add(id)
        listener.onDataSetChanged()
        notifyItemChanged(position)
    }

    /**
     * Clear [selectedIdSet] and then notifies listeners of changes.
     *
     */
    fun clearSelectedThenNotify() {
        selectedPositionSet.clear()
        selectedIdSet.clear()
        notifyDataSetChanged()
    }

    abstract inner class MultiSelectViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setSafeOnClickListener {
                if (inSelectionMode) toggleSelectedThenNotify(adapterPosition)
                else listener.onMultiSelectAdapterInteraction(adapterPosition)
            }
            view.setOnLongClickListener {
                toggleSelectedThenNotify(adapterPosition); true
            }
        }
    }

    abstract override fun getItemCount(): Int

    /**
     * Interface which must be implemented to get notified of data changes and adapter interaction
     *
     */
    interface MultiSelectAdapterItemInteractionListener {

        /**
         * Gets called when an item was selected or deselected.
         */
        fun onDataSetChanged()

        /**
         * Gets called when an item was clicked on while adapter is not [inSelectionMode]
         *
         * @param position The position of the item that was clicked.
         */
        fun onMultiSelectAdapterInteraction(position: Int)

    }
}
