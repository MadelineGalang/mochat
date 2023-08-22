package com.speakbyhand.app.core

class LimitedDeque<T>(var maxCount: Int) {
    private var items = MutableList<T?>(maxCount){null}
    private var count: Int = 0
    private var topIndex: Int = -1
    private var bottomIndex: Int = 0

    fun top(): T? {
        return items[topIndex]
    }

    fun bottom(): T? {
        return items[bottomIndex]
    }

    fun push(item: T) {
        topIndex = getNextIndex(topIndex)
        items[topIndex] = item
        if (isFull) {
            bottomIndex = getNextIndex(bottomIndex)
        } else {
            count += 1
        }
    }

    fun clear() {
        for (i in 0 until maxCount) {
            items[i] = null
        }
        topIndex = -1
        bottomIndex = 0
        count = 0
    }

    fun pop(): T? {
        if (isEmpty) {
            return null
        }
        val item = items[topIndex]
        count -= 1
        topIndex = getPreviousIndex(topIndex)
        return item
    }

    private fun getNextIndex(index: Int): Int {
        var nextIndex = index + 1
        if (nextIndex >= maxCount) {
            nextIndex = 0
        }
        return nextIndex
    }

    private fun getPreviousIndex(index: Int): Int {
        var previousIndex = index - 1
        if (previousIndex < 0) {
            previousIndex = maxCount - 1
        }
        return previousIndex
    }

    val isFull: Boolean
        get() = count == maxCount
    val isEmpty: Boolean
        get() = count == 0
    val forwardIterator: Iterator<T?>
        get() = ForwardIterator()
    val backwardIterator: Iterator<T?>
        get() = BackwardIterator()

    internal inner class ForwardIterator : Iterator<T?> {
        var currentIndex = bottomIndex
        var iterationCount = 0
        override fun hasNext(): Boolean {
            return iterationCount < maxCount
        }

        override fun next(): T? {
            val top = items[currentIndex]
            currentIndex = getNextIndex(currentIndex)
            iterationCount++
            return top
        }
    }

    internal inner class BackwardIterator : Iterator<T?> {
        var currentIndex = topIndex
        var iterationCount = 0
        override fun hasNext(): Boolean {
            return iterationCount < maxCount
        }

        override fun next(): T? {
            val top = items[currentIndex]
            currentIndex = getPreviousIndex(currentIndex)
            iterationCount++
            return top
        }
    }
}