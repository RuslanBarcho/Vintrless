package pw.vintr.vintrless.tools.list

class BoundedList<T>(private val maxSize: Int = Int.MAX_VALUE) : MutableList<T> {

    // Encapsulate a MutableList to delegate operations
    private val delegate = mutableListOf<T>()

    // Override methods to enforce bounded behavior
    override fun add(element: T): Boolean {
        if (delegate.size >= maxSize) {
            delegate.removeAt(0) // Remove the oldest element
        }
        return delegate.add(element)
    }

    override fun add(index: Int, element: T) {
        if (delegate.size >= maxSize) {
            delegate.removeAt(0) // Remove the oldest element
        }
        delegate.add(index, element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val elementsToAdd = elements.toList()
        val overflow = delegate.size + elementsToAdd.size - maxSize
        if (overflow > 0) {
            delegate.subList(0, overflow).clear() // Remove the oldest elements
        }
        return delegate.addAll(elementsToAdd)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val elementsToAdd = elements.toList()
        val overflow = delegate.size + elementsToAdd.size - maxSize
        if (overflow > 0) {
            delegate.subList(0, overflow).clear() // Remove the oldest elements
        }
        return delegate.addAll(index, elementsToAdd)
    }

    // Delegate all other MutableList methods to the encapsulated list
    override val size: Int get() = delegate.size
    override fun contains(element: T): Boolean = delegate.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)
    override fun get(index: Int): T = delegate.get(index)
    override fun indexOf(element: T): Int = delegate.indexOf(element)
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override fun iterator(): MutableIterator<T> = delegate.iterator()
    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)
    override fun listIterator(): MutableListIterator<T> = delegate.listIterator()
    override fun listIterator(index: Int): MutableListIterator<T> = delegate.listIterator(index)
    override fun remove(element: T): Boolean = delegate.remove(element)
    override fun removeAll(elements: Collection<T>): Boolean = delegate.removeAll(elements)
    override fun removeAt(index: Int): T = delegate.removeAt(index)
    override fun retainAll(elements: Collection<T>): Boolean = delegate.retainAll(elements)
    override fun set(index: Int, element: T): T = delegate.set(index, element)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = delegate.subList(fromIndex, toIndex)
    override fun clear() = delegate.clear()
}

inline fun <T> boundedListOf(maxSize: Int = Int.MAX_VALUE): BoundedList<T> = BoundedList(maxSize)

fun <T> boundedListOf(
    maxSize: Int,
    vararg elements: T
): BoundedList<T> = BoundedList<T>(maxSize).apply { addAll(elements) }

fun <T> boundedListOf(vararg elements: T): BoundedList<T> = BoundedList<T>().apply { addAll(elements) }
