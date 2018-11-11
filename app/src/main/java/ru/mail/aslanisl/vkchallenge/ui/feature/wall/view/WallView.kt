package ru.mail.aslanisl.vkchallenge.ui.feature.wall.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.customview.widget.ViewDragHelper.STATE_SETTLING
import com.vk.sdk.api.model.VKApiPhoto
import com.vk.sdk.api.model.VKApiVideo
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

    private val thumbHeight = resources.getDimensionPixelSize(R.dimen.image_normal_height)
    private val thumbWidth = resources.getDimensionPixelSize(R.dimen.image_normal_width)

    private val botContainerMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
    private val moreButtonHeight = resources.getDimensionPixelSize(R.dimen.wall_more_height)
    private val bottomExpandMargin = resources.getDimensionPixelSize(R.dimen.spacing_large)

    private val dragHelper: ViewDragHelper

    private var draggingBorder = 0
    private var draggingState = 0
    private var preventPositionReset = true

    var wallDragListener: WallDragListener? = null
    var moreClickListener: (() -> Unit)? = null

    private var viewClosed = false
    var dragEnable = true

    private var photoWidth: Int = 0
    private var currentPhotoPosition: Int = 0

    private val verticalRange by lazy {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        (size.x * 1.4).roundToInt()
    }

    init {
        inflate(context, R.layout.item_wall, this)

        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        photoWidth = size.x / 2

        dragHelper = ViewDragHelper.create(this, WallViewDragCallback())
    }

    fun initWall(wallModel: VKWallModel) {
        wallItem.visibility = View.VISIBLE
        wallItem.scrollable = false
        wallUserDate.text = DateManager.formatDateStamp(wallModel.date)
        val thumb = if (wallModel.profile != null) {
            wallModel.profile?.photo?.getImageForDimension(thumbWidth, thumbHeight)
        } else {
            wallModel.group?.photo?.getImageForDimension(thumbWidth, thumbHeight)
        }
        GlideApp.with(wallUserName).load(thumb).circleCrop().into(wallUserImage)

        initName(wallModel)
        initPhotos(wallModel)

        initText(wallModel.text)
    }

    private fun initName(wallModel: VKWallModel) {
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
    }

    private fun initPhotos(wallModel: VKWallModel) {
        val attachmentPhotos = mutableListOf<String>()
        wallModel.attachments.forEach { attachment ->
            val photo = when (attachment) {
                is VKApiVideo -> {
                    attachment.photo?.getOrNull((attachment.photo?.size ?: 0) - 1)?.src
                }
                is VKApiPhoto -> {
                    attachment.src?.getOrNull((attachment.src?.size ?: 0) - 1)?.src
                }
                else -> null
            }
            photo?.let { attachmentPhotos.add(it) }
        }
        indicatorView.setItemCount(attachmentPhotos.size)

        val firstPhoto = attachmentPhotos.getOrNull(0)
        if (firstPhoto.isNullOrEmpty().not()){
            wallImage.visibility = View.VISIBLE
            GlideApp.with(wallImage).load(firstPhoto).into(wallImage)
        } else {
            wallImage.visibility = View.GONE
        }

        if (attachmentPhotos.size > 1){
            nextImage.setOnClickListener { nextPhoto(attachmentPhotos) }
            prevImage.setOnClickListener { prevPhoto(attachmentPhotos) }
            indicatorView.visibility = View.VISIBLE
        } else {
            indicatorView.visibility = View.GONE
        }
    }

    private fun nextPhoto(photos: List<String>){
        val nextPosition = currentPhotoPosition + 1
        if (nextPosition <= photos.size - 1){
            GlideApp.with(wallImage).load(photos[nextPosition]).into(wallImage)
            currentPhotoPosition ++
        }
        indicatorView.selectItem(currentPhotoPosition)
    }

    private fun prevPhoto(photos: List<String>) {
        val prevPosition = currentPhotoPosition - 1
        if (prevPosition >= 0){
            GlideApp.with(wallImage).load(photos[prevPosition]).into(wallImage)
            currentPhotoPosition --
        }
        indicatorView.selectItem(currentPhotoPosition)
    }

    private fun initText(text: String) {
        itemWallText.text = text
        if (text.isEmpty()){
            itemWallMore.visibility = View.GONE
            return
        }
        itemWallText.post {
            val viewHeight = wallItem.height - paddingBottom - paddingTop - botContainerMargin * 2
            val maxTextHeight = viewHeight - wallTopContainer.height - wallImage.height - botContainerMargin * 2
            val textHeight = itemWallText.height
            if (textHeight >= maxTextHeight) {
                val maxTextHeightWithMore = maxTextHeight - botContainerMargin * 2 - moreButtonHeight
                var maxLines = (maxTextHeightWithMore * itemWallText.lineCount) / textHeight.toDouble()
                if (maxLines < 0) maxLines = 0.0
                addEllipsize(maxLines)

                itemWallMore.setOnClickListener { showMore() }
            } else {
                clearEllipsize()
            }
        }
    }

    private fun showMore() {
        (wallBottomContainer.layoutParams as MarginLayoutParams).bottomMargin = bottomExpandMargin
        wallItem.scrollable = true
        clearEllipsize()
        moreClickListener?.invoke()
    }

    private fun addEllipsize(maxLines: Double) {
        itemWallText.maxLines = Math.floor(maxLines).toInt()
        itemWallText.ellipsize = TextUtils.TruncateAt.END
        itemWallMore.visibility = View.VISIBLE
    }

    private fun clearEllipsize() {
        itemWallText.maxLines = Int.MAX_VALUE
        itemWallText.ellipsize = null
        itemWallMore.visibility = View.GONE
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