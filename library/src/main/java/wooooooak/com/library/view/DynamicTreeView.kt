package wooooooak.com.library.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.forEach
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import wooooooak.com.library.R
import wooooooak.com.library.adapter.DynamicTreeAdapter
import wooooooak.com.library.data.FLING
import wooooooak.com.library.data.UniqueKey
import wooooooak.com.library.ext.clearBackground
import wooooooak.com.orgviewsample.utils.getValueAnimator


class DynamicTreeView @JvmOverloads constructor(
    _context: Context,
    private val _attributeSet: AttributeSet? = null,
    _defStyle: Int = 0
) : ConstraintLayout(
    _context,
    _attributeSet,
    _defStyle
), GestureDetector.OnGestureListener {

    var rootViewWidth = 10

    var treeData: List<UniqueKey>? = null

    var pageMargin = resources.getDimensionPixelSize(R.dimen.org_default_page_marge)

    var orgAdapterGenerator: (() -> DynamicTreeAdapter<*>)? = null

    var colorResourceList: IntArray = intArrayOf()

    var animationDuration = 250L

    private var isUserDraging = false

    private val mDetector: GestureDetectorCompat

    private var mDownX = 0f

    private var prevDownX = 0f

    private var maxDepthCount = 3

    private var isAnimating = false

    private var flingType = FLING.NONE

    private val animationInterpolator: Interpolator = LinearInterpolator()

    private val recyclerViewStack = mutableListOf<View>()

    private val recyclerViewCount: Int
        get() = recyclerViewStack.size

    private val visibleViewList: List<View>
        get() = recyclerViewStack.filter { it.visibility == View.VISIBLE }

    private val visibleViewCount: Int
        get() = recyclerViewStack.count { it.visibility == View.VISIBLE }

    init {
        initAttrs()
        mDetector = GestureDetectorCompat(context, this)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mDetector.onTouchEvent(ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_UP -> {
                if (isUserDraging && recyclerViewCount > 2) {
                    val mSlop = rootViewWidth / 6
                    if (flingType == FLING.LEFT) {
                        cancelDragToRight()
                    } else if (mDownX + mSlop < ev.rawX || flingType == FLING.RIGHT) {
                        doIfNotAnimating {
                            moveContinue()
                        }
                    } else {
                        if (recyclerViewCount > 2) {
                            val viewToMove = recyclerViewStack[recyclerViewCount - 2]
                            viewToMove.updateLayoutParams<LayoutParams> {
                                horizontalBias = 0f
                                marginEnd = 0
                            }
                        }
                    }
                    prevDownX = 0f
                    isUserDraging = false
                    flingType = FLING.NONE
                }
            }
        }
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        flingType = if (velocityX > 1500) FLING.RIGHT else FLING.LEFT
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        doIfNotAnimating {
            if (visibleViewCount > 2) {
                isUserDraging = true
                mDownX = e1.rawX
                val deltaX = e2.rawX - e1.rawX
                if (deltaX > 0) onDragListView(e1.rawX, e2.rawX)
            }
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    fun render() {
        initView()
    }

    fun goBack() {
        doIfNotAnimating {
            movePrevRecyclerViewToRight {
                removeLastView()
                updateColorOfVisibleView()
            }
        }
    }

    fun canGoBack() = recyclerViewCount > 1

    private fun initView() {
        val rootList = treeData ?: listOf()
        val recyclerView = getNewRecyclerViewWrapper(rootList).apply {
            setBackgroundColor(colorResourceList[colorResourceList.size - 1])
        }
        recyclerViewStack.add(recyclerView)
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(recyclerView, lp)
    }

    private fun cancelDragToRight() {
        val viewToMove = recyclerViewStack[recyclerViewCount - 2]
        viewToMove.updateLayoutParams<LayoutParams> {
            horizontalBias = 0f
        }
    }

    private fun onDragListView(downX: Float, touchedX: Float) {
        if (visibleViewCount > 2) {
            val rightMargin = (visibleViewCount - 1) * pageMargin
            val viewToMove = recyclerViewStack[recyclerViewCount - 2]
            val parent = rootViewWidth
            val delta = touchedX - downX
            viewToMove.updateLayoutParams<LayoutParams> {
                horizontalBias = (delta / parent) * 2
                marginEnd = rightMargin
            }
            prevDownX = touchedX
        } else if (visibleViewCount == 2) {
            // TODO 처음 뷰로 돌아갈 때 처리
        }
    }

    private fun moveContinue() {
        val viewToMove = recyclerViewStack[recyclerViewCount - 2]
        viewToMove.updateLayoutParams<LayoutParams> {
            marginEnd = 0
            marginStart = 0
            width = (rootViewWidth * 0.5).toInt()
        }
        val biasRatio = 1f - (viewToMove.layoutParams as LayoutParams).horizontalBias
        val gapRatio = if (biasRatio > 0) biasRatio else 1f
        makeGoneViewToVisible(recyclerViewCount - 1)
        isAnimating = true
        getValueAnimator(true, animationDuration, LinearInterpolator()) {
            viewToMove.updateLayoutParams<LayoutParams> {
                horizontalBias += (gapRatio * it)
            }
            if (it == 1f) {
                viewToMove.updateLayoutParams<LayoutParams> {
                    startToEnd = recyclerViewStack[recyclerViewCount - 3].id
                    startToStart = LayoutParams.UNSET
                    endToEnd = LayoutParams.UNSET
                }
                val deletedView = recyclerViewStack.removeAt(recyclerViewCount - 1)
                removeView(deletedView)
                updateColorOfVisibleView()
                isAnimating = false
            }
        }.start()
    }

    private fun movePrevRecyclerViewToRight(onMoved: () -> Unit) {
        updateColorOfVisibleView()
        val currentDepth = recyclerViewStack.size - 1
        val toMovingView = recyclerViewStack[currentDepth - 1]
        toMovingView.findViewById<RecyclerView>(R.id.recycler_view).forEach {
            it.background = null
        }
        makeGoneViewToVisible(currentDepth)
        if (currentDepth != 1) {
            toMovingView.updateLayoutParams {
                width = rootViewWidth / 2
            }
            isAnimating = true
            getValueAnimator(true, animationDuration, animationInterpolator) {
                toMovingView.updateLayoutParams<LayoutParams> {
                    horizontalBias = it
                    marginStart = ((1f - it) * pageMargin).toInt()
                }
                if (it == 1f) {
                    toMovingView.updateLayoutParams<LayoutParams> {
                        startToStart = LayoutParams.UNSET
                        startToEnd = recyclerViewStack[currentDepth - 2].id
                        endToEnd = LayoutParams.UNSET
                    }
                    onMoved()
                    isAnimating = false
                }
            }.start()
        } else {
            recyclerViewStack[0].updateLayoutParams<LayoutParams> {
                width = LayoutParams.MATCH_PARENT
            }
            onMoved()
        }
    }

    private fun getNewRecyclerViewWrapper(itemList: List<UniqueKey>): ConstraintLayout {
        val wrapperView =
            LayoutInflater.from(context).inflate(R.layout.list_ograniztion, null, false)
                .apply {
                    id = View.generateViewId()
                    val groupListAdapter = (orgAdapterGenerator?.invoke() as DynamicTreeAdapter<UniqueKey>).apply {
                        currentDepth = recyclerViewCount
                        onClickGroupListener = { uniqueKey, depth ->
                            doIfNotAnimating { onClickGroupName(uniqueKey, depth) }
                        }
                    }
                    findViewById<RecyclerView>(R.id.recycler_view).run {
                        this?.adapter = groupListAdapter
                        groupListAdapter.submitList(itemList)
                    }
                } as ConstraintLayout

        return wrapperView
    }

    private fun updateColorOfVisibleView() {
        val colorListCount = colorResourceList.size
        val startColorIndex = colorListCount - visibleViewCount
        visibleViewList
            .forEachIndexed { index, view ->
                view.setBackgroundColor(colorResourceList[startColorIndex + index])
            }
    }

    private fun makeGoneViewToVisible(currentDepth: Int) {
        if (currentDepth > maxDepthCount) {
            recyclerViewStack[currentDepth - 3].visibility = View.VISIBLE
        }
    }

    private fun removeLastView() {
        val lastView = recyclerViewStack.removeAt(recyclerViewCount - 1)
        removeView(lastView)
    }

    private fun getChildGroups(groupName: String): List<UniqueKey> {
        val queue = mutableListOf<UniqueKey>().apply {
            treeData?.forEach {
                add(it)
            }
        }
        while (queue.isNotEmpty()) {
            val header = queue.removeAt(0)
            if (header.uniqueKey == groupName) {
                return header.child ?: listOf()
            } else {
                header.child?.forEach { queue.add(it) }
            }
        }
        return listOf()
    }

    private fun addGroupListView(uniquKey: String, preViewDepth: Int) {
        val searchedPartList = getChildGroups(uniquKey)
        val recyclerViewWrapper = getNewRecyclerViewWrapper(searchedPartList)
        recyclerViewStack.add(recyclerViewWrapper)
        val lp = LayoutParams(
            (rootViewWidth * 0.5).toInt(),
            LayoutParams.MATCH_PARENT
        )
        addView(recyclerViewWrapper, lp)
        updateColorOfVisibleView()
        recyclerViewWrapper.updateLayoutParams<LayoutParams> {
            startToEnd = recyclerViewStack[preViewDepth].id
            endToStart = LayoutParams.UNSET
            endToEnd = LayoutParams.UNSET
        }
    }

    private fun onClickGroupName(groupName: String, depth: Int) {
        when {
            isClickFirstTime() -> {
                rootViewWidth = recyclerViewStack[0].width
                addGroupListView(groupName, 0)
                isAnimating = true
                getValueAnimator(false, animationDuration, animationInterpolator) {
                    recyclerViewStack[0].updateLayoutParams {
                        width = ((rootViewWidth * 0.5) + ((rootViewWidth * it) * 0.5)).toInt()
                    }
                    if (it == 0f) isAnimating = false
                }.start()
            }
            isClickNewItem(depth) -> {
                if (depth >= maxDepthCount) makeLastThirdViewGone()
                recyclerViewStack.clearChildViewBackgroundAt(recyclerViewCount - 1)
                addGroupListView(groupName, depth)
                moveViewToLeft(depth)
            }
            else -> {
                recyclerViewStack.clearChildViewBackgroundAt(recyclerViewCount - 2)
                val toRemovedListView = recyclerViewStack.removeAt(recyclerViewCount - 1)
                removeView(toRemovedListView)
                addGroupListView(groupName, depth)
            }
        }
    }

    private fun makeLastThirdViewGone() {
        recyclerViewStack[recyclerViewCount - 3].visibility = View.GONE
    }

    private fun doIfNotAnimating(action: () -> Unit) {
        if (!isAnimating) {
            action()
        }
    }

    private fun moveViewToLeft(toBeMovedDepth: Int) {
        val viewWidth = recyclerViewStack[toBeMovedDepth].width
        val stackedPageCount = visibleViewCount - 2
        recyclerViewStack[toBeMovedDepth].updateLayoutParams<LayoutParams> {
            startToStart = recyclerViewStack[toBeMovedDepth - 1].id
            endToEnd = LayoutParams.PARENT_ID
            horizontalBias = 1f
        }
        isAnimating = true
        getValueAnimator(false, animationDuration, animationInterpolator) {
            recyclerViewStack[toBeMovedDepth].updateLayoutParams<LayoutParams> {
                horizontalBias = it
                width = viewWidth - ((1f - it) * (pageMargin * stackedPageCount)).toInt()
                marginStart = ((1f - it) * pageMargin).toInt()
            }
            if (it == 0f) isAnimating = false
        }.start()
        recyclerViewStack[toBeMovedDepth]
    }

    private fun isClickFirstTime() = recyclerViewCount == 1

    private fun isClickNewItem(depth: Int) = depth > 0 && depth == recyclerViewCount - 1

    private fun List<View>.clearChildViewBackgroundAt(index: Int) {
        get(index).findViewById<RecyclerView>(R.id.recycler_view).clearBackground()
    }

    private fun initAttrs() {
        _attributeSet?.let {
            context.obtainStyledAttributes(_attributeSet, R.styleable.DynamicTreeView).run {
                pageMargin = getDimensionPixelSize(R.styleable.DynamicTreeView_dt_list_page_margin, pageMargin)
                val tempMaxDepthCount = getInt(R.styleable.DynamicTreeView_dt_depth_limit, 3)
                maxDepthCount = if (tempMaxDepthCount < 3) maxDepthCount else tempMaxDepthCount
                animationDuration = getInteger(R.styleable.DynamicTreeView_dt_animation_duration, 250).toLong()
                val colorsId = getResourceId(R.styleable.DynamicTreeView_dt_stack_view_color_array, 0)
                colorResourceList = resources.getIntArray(colorsId)
                // If the number of views shown is not the same as the number of color lists, they are all white.
                if (maxDepthCount + 1 != colorResourceList.size) {
                    colorResourceList = IntArray(maxDepthCount + 1) {
                        ContextCompat.getColor(context, R.color.orgWhite)
                    }
                }
                recycle()
            }
        }
    }

}