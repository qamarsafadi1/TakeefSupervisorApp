package com.selsela.takeefapp.data.auth.source.remote

import com.selsela.takeefapp.data.auth.model.address.AddressResponse
import com.selsela.takeefapp.data.auth.model.auth.AuthResponse
import com.selsela.takeefapp.data.auth.model.general.GeneralResponse
import com.selsela.takeefapp.data.auth.model.notifications.NotificationResponse
import com.selsela.takeefapp.data.auth.model.support.SupportResponse
import com.selsela.takeefapp.data.auth.model.support.contacts.ContactsResponse
import com.selsela.takeefapp.data.auth.model.wallet.WalletResponse
import com.selsela.takeefapp.utils.LocalData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface AuthApi {
    @POST("supervisor/auth_mobile_only")
    @JvmSuppressWildcards
    @FormUrlEncoded
    suspend fun auth(
        @FieldMap
        body: Map<String, Any>
    ): Response<AuthResponse>

    @POST("supervisor/verify_code")
    @JvmSuppressWildcards
    @FormUrlEncoded
    suspend fun verifyCode(
        @FieldMap
        body: Map<String, Any>
    ): Response<AuthResponse>
   @POST("supervisor/resend_verify_code")
    @JvmSuppressWildcards
    @FormUrlEncoded
    suspend fun resendCode(
        @FieldMap
        body: Map<String, Any>
    ): Response<AuthResponse>

    @GET("supervisor/me")
    suspend fun me(): Response<AuthResponse>

    @GET("supervisor/supervisor_wallet")
    suspend fun getWallet(): Response<WalletResponse>

    @POST("supervisor/update_supervisor_fcm_token")
    @FormUrlEncoded
    suspend fun updateUserFCM(
        @Field("token") token: String? = LocalData.fcmToken
    ): Response<GeneralResponse>

    @POST("supervisor/update_fcm_token")
    @FormUrlEncoded
    suspend fun updateFCM(
        @Field("token") token: String? = LocalData.fcmToken
    ): Response<GeneralResponse>

    @POST("supervisor/update_profile")
    @JvmSuppressWildcards
    @Multipart
    suspend fun updateProfile(
        @Part
        avatar: MultipartBody.Part?,
        @PartMap
        body: HashMap<String, RequestBody>
    ): Response<AuthResponse>

    @GET("supervisor/get_supervisor_notifications")
    suspend fun getNotifications(): Response<NotificationResponse>

    @GET("user/get_user_addresses")
    suspend fun getAddress(): Response<AddressResponse>

    @POST("supervisor/delete_notification")
    @FormUrlEncoded
    suspend fun deleteNotification(
        @Field("notification_id") notificationId: Int
    ): Response<NotificationResponse>

    @POST("app/contact_or_reply")
    @JvmSuppressWildcards
    @FormUrlEncoded
    suspend fun contactAdmin(
        @FieldMap
        body: Map<String, Any>
    ): Response<SupportResponse>

    @GET("app/get_contacts")
    suspend fun getContacts(): Response<ContactsResponse>

}