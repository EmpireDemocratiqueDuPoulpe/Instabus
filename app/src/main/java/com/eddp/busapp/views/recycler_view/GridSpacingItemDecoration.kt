package com.eddp.busapp.views.recycler_view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(columnsCount: Int, spacing: Int, addEdge: Boolean = true)
    : RecyclerView.ItemDecoration() {
    private var _columnsCount: Int = columnsCount
    private var _spacing: Int = spacing
    private var _addEdge: Boolean = addEdge

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPos: Int = parent.getChildAdapterPosition(view)
        val itemColumn: Int = itemPos % this._spacing

        if (this._addEdge) {
            // Top / Bottom
            if (itemPos < this._columnsCount) {
                outRect.top = this._spacing
            }

            outRect.bottom = this._spacing

            // Side
            outRect.right = ((itemColumn + 1) * ((1f / this._columnsCount) * this._spacing)).toInt()
            outRect.left = (this._spacing - itemColumn * ((1f / this._columnsCount) * this._spacing)).toInt()
        } else {
            // Top / Bottom
            if (itemPos >= this._columnsCount) {
                outRect.top = this._spacing
            }

            // Side
            outRect.right = (this._spacing - (itemColumn + 1) * ((1f / this._columnsCount) * this._spacing)).toInt()
            outRect.left = (itemColumn * ((1 / this._columnsCount) * this._spacing)).toInt()
        }
    }
}