package ru.mail.aslanisl.vkchallenge.ui.feature.wall.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.activity_wall.*
import ru.mail.aslanisl.vkchallenge.R
import ru.mail.aslanisl.vkchallenge.data.base.UIData
import ru.mail.aslanisl.vkchallenge.data.model.VKWallModel
import ru.mail.aslanisl.vkchallenge.domain.di.component.AppComponent
import ru.mail.aslanisl.vkchallenge.extensions.forChildEachReverse
import ru.mail.aslanisl.vkchallenge.extensions.toast
import ru.mail.aslanisl.vkchallenge.ui.base.activity.BaseActivity
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.model.WallViewModel
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.view.DragDirection
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.view.WallDragListener
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.view.WallView

class WallActivity : BaseActivity() {

    override fun injectDI(appComponent: AppComponent) = appComponent.inject(this)

    private val viewModel by lazy { initViewModel<WallViewModel>() }
    private val wallsObserver by lazy {
        Observer<UIData<List<VKWallModel>>> {
            it ?: return@Observer
            initWallsData(it)
        }
    }

    private val walls = mutableListOf<VKWallModel>()
    private var currentWallId: Int = 0
    private var currentWallPosition: Int = 0
    private val currentWall: VKWallModel?
        get() = walls.getOrNull(currentWallPosition)

    private val wallContainerMargin by lazy {
        resources.getDimensionPixelSize(R.dimen.wall_container_margin)
    }
    private val expandSet by lazy {
        val set = ConstraintSet()
        set.clone(wallMainContainer)
        set.connect(wallContainer.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        set
    }
    private val collapseSet by lazy {
        val set = ConstraintSet()
        set.clone(wallMainContainer)
        set.connect(wallContainer.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, wallContainerMargin)
        set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)

        loadWalls()
        initViews()
    }

    private fun loadWalls(){
        viewModel.loadWalls().observe(this, wallsObserver)
    }

    private fun initViews() {
        wallClose.setOnClickListener {
            viewModel.skipWall(currentWall)
            showNextWall()
        }
        wallLike.setOnClickListener {
            viewModel.likeWall(currentWall)
            showNextWall()
        }
    }

    private fun initWallsData(data: UIData<List<VKWallModel>>) {
        when {
            data.isError() -> toast(data.throwable?.message)
            data.isSuccess() -> initWalls(data.body)
        }
    }

    private fun initWalls(walls: List<VKWallModel>?) {
        walls ?: return
        this.walls += walls
        // If no walls now. Show 2
        if (wallContainer.childCount == 0){
            showNextWall(false)
            showNextWall(false)
        }
    }

    private fun showNextWall(closeTop: Boolean = true) {
        if (closeTop) {
            val topView = wallContainer.getChildAt(wallContainer.childCount - 1) as? WallView
            topView?.let { closeView(it) }
        }
        val position = currentWallPosition
        val nextWall = walls.getOrNull(position)
        if (nextWall != null) {
            addWall(nextWall)
            currentWallPosition++
        }
        checkIfNeedLoadMore()
    }

    private fun checkIfNeedLoadMore(){
        if (walls.size - currentWallPosition <= 3){
            loadWalls()
        }
    }

    private fun addWall(wall: VKWallModel) {
        val wallView = WallView(this)
        wallView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        wallView.initWall(wall)
        wallContainer.addView(wallView, 0)
        currentWallId = wall.postId
        wallView.wallDragListener = object : WallDragListener {
            override fun startDrag(direction: DragDirection) {
                likeButton.visibility = if (direction == DragDirection.LEFT) View.GONE else View.VISIBLE
                skipButton.visibility = if (direction == DragDirection.RIGHT) View.GONE else View.VISIBLE
            }

            override fun stopDrag(direction: DragDirection, closing: Boolean) {
                likeButton.visibility = View.GONE
                skipButton.visibility = View.GONE
            }

            override fun close(direction: DragDirection) {
                showNextWall()
                if (direction == DragDirection.RIGHT) viewModel.likeWall(wall) else viewModel.skipWall(wall)
            }
        }
        wallView.moreClickListener = {
            wallView.dragEnable = false
            expandContainer()
        }

        enableDragForView()
    }

    private fun expandContainer() {
        TransitionManager.beginDelayedTransition(wallMainContainer)
        expandSet.applyTo(wallMainContainer)
    }

    private fun collapseContainer() {
        collapseSet.applyTo(wallMainContainer)
    }

    private fun closeView(view: WallView){
        collapseContainer()
        wallContainer.removeView(view)
        likeButton.visibility = View.GONE
        skipButton.visibility = View.GONE
    }

    // Sometimes view under top view handle drag
    private fun enableDragForView(){
        val childCount = wallContainer.childCount
        wallContainer.forChildEachReverse { view, index ->
            if (view is WallView) view.dragEnable = index == childCount - 1
        }
    }
}
