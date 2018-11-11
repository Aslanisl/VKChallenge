package ru.mail.aslanisl.vkchallenge.ui.feature.wall.view

interface WallDragListener {

    fun startDrag(direction: DragDirection)
    /**
     * @param closing if view going to close soon
     */
    fun stopDrag(direction: DragDirection, closing: Boolean)
    fun close(direction: DragDirection)
}