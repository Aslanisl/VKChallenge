package ru.mail.aslanisl.vkchallenge.ui.feature.wall.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.customview.widget.ViewDragHelper.STATE_SETTLING
import com.vk.sdk.api.model.VKApiPhoto
import kotlinx.android.synthetic.main.item_wall.view.*
import ru.mail.aslanisl.vkchallenge.R
import ru.mail.aslanisl.vkchallenge.data.model.VKWallModel
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.manager.DateManager
import ru.mail.aslanisl.vkchallenge.utils.GlideApp
import kotlin.math.roundToInt

private const val AUTO_OPEN_SPEED_LIMIT = 500
private const val MAX_ROTATION = 30
private const val SCALE_FACTOR = 0.25
private const val AUTO_CLOSE_FACTOR = 0.25

class WallView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val thumbHeight = context.resources.getDimensionPixelSize(R.dimen.image_normal_height)
    private val thumbWidth = context.resources.getDimensionPixelSize(R.dimen.image_normal_width)

    private val dragHelper: ViewDragHelper

    private var draggingBorder = 0
    private var draggingState = 0
    private var preventPositionReset = true

    var wallDragListener: WallDragListener? = null

    private var viewClosed = false

    var dragEnable = true

    private val verticalRange by lazy {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        (size.x * 1.4).roundToInt()
    }

    init {
        inflate(context, R.layout.item_wall, this)

        dragHelper = ViewDragHelper.create(this, WallViewDragCallback())
    }

    fun initWall(wallModel: VKWallModel) {
        wallItem.visibility = View.VISIBLE
        wallUserDate.text = DateManager.formatDateStamp(wallModel.date)
        val thumb = if (wallModel.profile != null) {
            wallModel.profile?.photo?.getImageForDimension(thumbWidth, thumbHeight)
        } else {
            wallModel.group?.photo?.getImageForDimension(thumbWidth, thumbHeight)
        }
        GlideApp.with(wallUserName).load(thumb).circleCrop().into(wallUserImage)

        val name = if (wallModel.profile != null) {
            val firstName = wallModel.profile?.first_name
            val lastName = wallModel.profile?.last_name
            val builder = StringBuilder()
            if (firstName.isNullOrEmpty().not()) builder.append(firstName).append(" ")
            if (lastName.isNullOrEmpty().not()) builder.append(lastName)
            builder.toString()
        } else {
            wallModel.group?.name
        }
        wallUserName.text = name

        indicatorView.setItemCount(wallModel.attachments.size)
        val firstPhoto = wallModel.attachments.firstOrNull { it is VKApiPhoto }
        if (firstPhoto is VKApiPhoto) {
            val photo = firstPhoto.src[firstPhoto.src.size - 1]
            GlideApp.with(wallImage).load(photo.src).into(wallImage)
        }
        itemWallText.text = wallModel.text
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isMoving()) {
            dragHelper.processTouchEvent(event)
            true
        } else {
            false
        }
    }

    override fun computeScroll() {
        preventPositionReset = if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
            false
        } else {
            true
        }
    }

    private fun isMoving(): Boolean {
        return preventPositionReset ||
            draggingState == ViewDragHelper.STATE_DRAGGING ||
            draggingState == ViewDragHelper.STATE_SETTLING
    }

    inner class WallViewDragCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int) = dragEnable

        override fun getViewVerticalDragRange(child: View) = verticalRange

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int) = left

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val rangeToCheck = verticalRange
            val vel = Math.abs(xvel)
            val border = Math.abs(draggingBorder)
            val settleToOpen = when {
                vel > AUTO_OPEN_SPEED_LIMIT -> true
                vel < -AUTO_OPEN_SPEED_LIMIT -> false
                border > rangeToCheck * AUTO_CLOSE_FACTOR -> true
                border < rangeToCheck * AUTO_CLOSE_FACTOR -> false
                else -> false
            }
            val direction = if (draggingBorder < 0) -1 else 1

            val settleDestX = if (settleToOpen) verticalRange else 0

            wallDragListener?.stopDrag(
                if (draggingBorder > 0) DragDirection.RIGHT else DragDirection.LEFT,
                settleToOpen
            )

            if (dragHelper.settleCapturedViewAt(settleDestX * direction, 0)) {
                ViewCompat.postInvalidateOnAnimation(this@WallView)
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            if (draggingState != STATE_SETTLING){
                wallDragListener?.startDrag(if (left > 0) DragDirection.RIGHT else DragDirection.LEFT)
            }
            draggingBorder = left
            val diff = left / verticalRange.toFloat()
            changedView.rotation = diff * MAX_ROTATION
            val scale = (1 + diff * SCALE_FACTOR).toFloat()
            changedView.scaleX = scale
            changedView.scaleY = scale

            if (Math.abs(diff) >= 1 && viewClosed.not()) {
                viewClosed = true
                wallDragListener?.close(if (left > 0) DragDirection.RIGHT else DragDirection.LEFT)
                dragHelper.abort()
            }
        }

        override fun onViewDragStateChanged(state: Int) {
            draggingState = state
        }
    }
}