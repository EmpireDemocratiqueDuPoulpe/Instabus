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
        val itemPos = parent.getChildAdapterPosition(view)
        val itemColumn: Int = itemPos % this._columnsCount

        if (this._addEdge) {
            // Top / Bottom
            if (itemPos < this._columnsCount) {
                outRect.top = this._spacing
            }

            outRect.bottom = this._spacing

            // Left / Right
            outRect.left = this._spacing - itemColumn * this._spacing / this._columnsCount
            outRect.right = (itemColumn + 1) * this._spacing / this._columnsCount

        } else {
            // Top / Bottom
            if (itemPos >= this._columnsCount) {
                outRect.top = this._spacing
            }

            // Left / Right
            outRect.left = itemColumn * this._spacing / this._columnsCount
            outRect.right = this._spacing - (itemColumn + 1) * this._spacing / this._columnsCount
        }
    }
}