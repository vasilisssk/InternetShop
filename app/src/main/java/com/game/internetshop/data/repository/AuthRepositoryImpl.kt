package com.game.internetshop.data.repository

import android.util.Log
import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.User
import com.game.internetshop.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import utils.Utils

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient
): AuthRepository {
    override suspend fun login(email: String, password: String): AuthResult<User> {
        return try {
            Log.w("supabase_login", "Curr session: ${supabaseClient.auth.currentSessionOrNull().toString()}")
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            Log.w("supabase_login", "Before currentUser")
            val user = supabaseClient.auth.currentUserOrNull()
            if (user == null) {
                return AuthResult.Error("General")
            }
            Log.w("supabase_login", "After currentUser")

            Log.w("supabase_login", "Before SQL query")

            @Serializable
            data class QueryUserData (
                var user_id: Int?,
                var user_name: String?
            )

            var queryUserData = QueryUserData(null, null)
            try {
                queryUserData = supabaseClient.postgrest
                    .from("users")
                    .select(columns = Columns.list("user_id", "user_name")) {
                        filter { eq("auth_uid", user.id) }
                    }
                    .decodeSingle<QueryUserData>()
            } catch (e: Exception) {
                Log.w("supabase_login", "Error: "+e.message.toString())
            }

            Log.w("supabase_login", "After SQL query: ${queryUserData.user_id} || ${queryUserData.user_name}")

            AuthResult.Success(User(queryUserData.user_id!!,user.email!!, queryUserData.user_name!!))
        } catch (e: Exception) {
            Log.e("supabase_login", e.message.toString())
            when {
                e.message.toString().contains("Invalid login credentials") -> AuthResult.Error("Data")
                else -> AuthResult.Error("General")
            }
        }
    }

    override suspend fun register(name: String, phoneNumber: String, email: String, password: String): AuthResult<User> {
        return try {

            Log.w("supabase_register", "Before checking phone")

            val isPhoneExists = isPhoneNumberExists("+7$phoneNumber")
            if (isPhoneExists) {
                Log.e("supabase_register", "Phone exists")
                return AuthResult.Error("Phone")
            }

            Log.w("supabase_register", "After checking phone")

            val preparedName = Utils.prepareName(name)

            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", preparedName)
                    put("phone", "+7$phoneNumber")
                }
            }

            val currentUser = supabaseClient.auth.currentUserOrNull()
            Log.w("supabase_register", "Current user is null: " + (currentUser == null).toString())

            val authUserUUID: String? = currentUser?.id
            if (authUserUUID == null) {
                return AuthResult.Error("General")
            }

            Log.w("supabase_register", "Before userProfile")

            val userProfile = UserProfile(email = email, userName = preparedName, phoneNumber = "+7$phoneNumber", roleId = 1, authUid =  authUserUUID)
            Log.w("supabase_register", "${userProfile.email} || ${userProfile.userName} || ${userProfile.phoneNumber} || ${userProfile.roleId} || ${userProfile.authUid}")

            var newUserId: Int? = null
            try {
                newUserId = supabaseClient.postgrest
                    .from("users")
                    .insert(userProfile){
                        select(Columns.list("user_id"))
                    }.decodeList<Map<String, Int>>()
                    .firstOrNull()
                    ?.get("user_id")
                Log.w("supabase_register", "User_id " + newUserId.toString())
            } catch (e: Exception) {
                Log.e("supabase_register", e.message.toString())
            }

            if (newUserId == null) {
                return AuthResult.Error("General")
            }

            AuthResult.Success(User(newUserId, email, preparedName))
        } catch (e: Exception) {
            Log.e("supabase_register", e.toString())
            when {
                e.message.toString().contains("User already registered") -> AuthResult.Error("Email")
                else -> AuthResult.Error("General")
            }
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            Log.w("supabase_logout", "Before getting currSession")
            val currSession = supabaseClient.auth.currentSessionOrNull()
            Log.w("supabase_logout", "After getting currSession: ${currSession?.user?.email}")
            if (currSession != null) {
                supabaseClient.auth.signOut(scope = SignOutScope.LOCAL)
                Log.w("supabase_logout", currSession.toString())
            }
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error("Failed signing out")
        }
    }

    override suspend fun getCurrentUserId(): AuthResult<Int> {
        return try {
            Log.w("supabase_getcurruserid", "Before getting id")
            val id = supabaseClient.postgrest
                .from("users")
                .select(Columns.list("user_id")) {
                    filter {
                        eq("auth_uid", supabaseClient.auth.currentUserOrNull()?.id!!)
                    }
                }
                .decodeList<Map<String,Int>>()
                .firstOrNull()
                ?.get("user_id")
            Log.w("supabase_getcurruserid", "After getting id: $id")
            if (id == null) {
                AuthResult.Error("Failed getting current user id")
            } else {
                AuthResult.Success(id)
            }
        } catch (e: Exception) {
            Log.e("supabase_getcurruserid", e.message.toString())
            AuthResult.Error("Failed getting current user id")
        }
    }

    override suspend fun isSessionActive(): AuthResult<Boolean> {
        return try {
            val isSessionActive = supabaseClient.auth.currentSessionOrNull()
            Log.w("supabase_issessionactive", (if (isSessionActive == null) false else true).toString())
            AuthResult.Success(if (isSessionActive == null) false else true)
        } catch (e: Exception) {
            Log.e("supabase_issessionactive", e.message.toString())
            AuthResult.Error("Error checking active session")
        }
    }

    override suspend fun getCurrentUser(): AuthResult<UserProfile> {
        return try {
            Log.w("supabase_getcurruser", "Before getting id")
            val profile = supabaseClient.postgrest
                .from("users")
                .select(Columns.list("email", "user_name", "phone_number", "role_id", "auth_uid")) {
                    filter {
                        eq("auth_uid", supabaseClient.auth.currentUserOrNull()?.id!!)
                    }
                }
                .decodeSingle<UserProfile>()
            Log.w("supabase_getcurruser", "After getting user profile. User name - ${profile.userName}")
            AuthResult.Success(profile)
        } catch (e: Exception) {
            Log.e("supabase_getcurruser", e.message.toString())
            AuthResult.Error("Failed getting current user")
        }
    }

    private suspend fun isPhoneNumberExists(phoneNumber: String): Boolean {
        return try {
            Log.w("supabase_isphonenumberexists", "Start")
            val result = supabaseClient.postgrest
                .rpc("check_phone_exists", mapOf("p_phone" to phoneNumber))
            Log.w("supabase_isphonenumberexists", "Result: ${result.data} || class: ${result.data.javaClass}")
            result.data.toBoolean()
        } catch (e: Exception) {
            Log.e("supabase_isphonenumberexists", e.message.toString())
            false
        }
    }
}