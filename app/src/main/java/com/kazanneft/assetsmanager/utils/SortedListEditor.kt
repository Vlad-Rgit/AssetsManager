package com.kazanneft.assetsmanager.utils

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.SortedList
import com.kazanneft.assetsmanager.models.Asset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 * Class for edit the sorted list
 */
class SortedListEditor<T>(val sortedList: SortedList<T>) {

    /**
     * Store here all actions to perform them then in commit method
     */
    private val actions = LinkedList< () -> Unit>()
    private var currentCommitAsync: Job? = null

    fun replaceAll(collection: Collection<T>): SortedListEditor<T> {
        actions.add{
            sortedList.replaceAll(collection)
        }
        return this
    }

    fun add(item: T): SortedListEditor<T> {
        actions.add {
            sortedList.add(item)
        }
        return this
    }

    fun addAll(collection: Collection<T>): SortedListEditor<T> {
        actions.add {
            sortedList.addAll(collection)
        }
        return this
    }

    fun clear(): SortedListEditor<T> {
        actions.add {
            sortedList.clear()
        }
        return this
    }

    /**
     * Perform all actions on sorted list async
     */
    fun commitAsync(): Job {

        return CoroutineScope(Dispatchers.Default).launch {

            //Wait for previous commit
            currentCommitAsync?.join()

            currentCommitAsync = launch {

                sortedList.beginBatchedUpdates()

                launch {
                    for (action in actions) {
                        action()
                    }
                }.join()

                sortedList.endBatchedUpdates()
            }
        }
    }


    /**
     * Perform all actions on sorted list
     */
    fun commit() {

        sortedList.beginBatchedUpdates()

        for(action in actions)
            action()

        Log.d("Adapter", "Sorted list updated")
        sortedList.endBatchedUpdates()
    }
}