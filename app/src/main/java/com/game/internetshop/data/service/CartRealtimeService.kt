// Самый простой и правильный вариант для 3.0.0
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class CartRealtimeService(private val supabaseClient: SupabaseClient) {
    private var channel: RealtimeChannel? = null

    suspend fun subscribeToCartChanges(userId: Int, onCartChanged: () -> Unit) {
        try {
            // Создаём канал с Id
            channel = supabaseClient.realtime.channel("cart_$userId")

            // Подписываемся на канал
            Log.w("supabase_subscribetocartchahges", "Before subscribing")
            channel!!.subscribe()
            Log.w("supabase_subscribetocartchahges", "After subscribing")

            // Настраиваем и собираем события
            channel!!.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "product_in_cart"
                filter(FilterOperation("user_id", FilterOperator.EQ, userId))
            }.collect { action ->
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
                    onCartChanged()
                }
            }
        } catch (e: Exception) {
            Log.e("supabase_subscribetocartchahges", e.message.toString())
        }

    }

    suspend fun unsubscribe() {
        channel?.unsubscribe()
        channel = null
    }
}