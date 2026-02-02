import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Dispatcher

class CartRealtimeService(private val supabaseClient: SupabaseClient) {
    private var channel: RealtimeChannel? = null

    // Flow для событий
    private val _cartEvents = MutableSharedFlow<Unit>()
    val cartEvents: SharedFlow<Unit> = _cartEvents.asSharedFlow()
    private var isSubscribed = false
    private var subscriptionJob: Job? = null

    suspend fun subscribeToCartChanges(userId: Int) {
        if (isSubscribed) return
        try {
            // Создаём канал с Id
            channel = supabaseClient.realtime.channel("cart_$userId")

            // Подписываемся на канал
            Log.w("supabase_subscribetocartchahges", "Before subscribing")
            channel!!.subscribe()
            isSubscribed = true
            Log.w("supabase_subscribetocartchahges", "After subscribing")

            // Запускаем сбор событий в отдельной корутине
            subscriptionJob = CoroutineScope(Dispatchers.IO).launch {
                channel?.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "product_in_cart"
                    filter(FilterOperation("user_id", FilterOperator.EQ, userId))
                }?.collect { action ->
                    val isOurUser = when (action) {
                        is PostgresAction.Insert -> {
                            val userIdFromRecord = action.record["user_id"]?.jsonPrimitive?.intOrNull
                            userIdFromRecord == userId
                        }
                        is PostgresAction.Update -> {
                            val userIdFromRecord = action.record["user_id"]?.jsonPrimitive?.intOrNull
                            userIdFromRecord == userId
                        }
                        is PostgresAction.Delete -> {
                            val userIdFromRecord = action.oldRecord["user_id"]?.jsonPrimitive?.intOrNull
                            userIdFromRecord == userId
                        }
                        else -> false
                    }

                    if (isOurUser) {
                        _cartEvents.emit(Unit) // кидаем события в Flow
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("supabase_subscribetocartchahges", e.message.toString())
            isSubscribed = false
        }

    }

    suspend fun unsubscribe() {
        subscriptionJob?.cancel()
        subscriptionJob = null
        channel?.unsubscribe()
        channel = null
        isSubscribed = false
    }
}