package pk.aspirasoft.core.db

/**
 * PersistentValue is a generic class which represents a single data value which is stored
 * in [PersistentStorage].
 *
 * @author saifkhichi96
 */
class PersistentValue<T : Any> {

    private val tag: String
    private val defValue: T?

    var value: T? = null
        set(value) {
            field = value
            save()
        }

    constructor(tag: String, type: Class<T>) {
        this.tag = tag
        this.defValue = null
        load(type)
    }

    constructor(tag: String, defValue: T) {
        this.tag = tag
        this.defValue = defValue
        load(defValue.javaClass)
    }

    private fun load(tClass: Class<T>) {
        val savedValue = PersistentStorage[tag, tClass]
        this.value = savedValue
    }

    fun save() {
        PersistentStorage.put(tag, value)
    }

    fun reset() {
        this.value = defValue
        save()
    }

}