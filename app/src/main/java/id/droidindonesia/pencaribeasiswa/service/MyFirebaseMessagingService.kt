package id.droidindonesia.pencaribeasiswa.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.droidindonesia.pencaribeasiswa.R
import id.droidindonesia.pencaribeasiswa.ui.BeasiswaActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
  val TAG = "FirebaseMessagingService"

  @SuppressLint("LongLogTag")
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    Log.d(TAG, "Dikirim dari: ${remoteMessage.from}")

    if (remoteMessage.notification != null) {
      showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
    }
  }

  private fun showNotification(title: String?, body: String?) {
    val intent = Intent(this, BeasiswaActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent,
        PendingIntent.FLAG_ONE_SHOT)

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannel(notificationManager)

    val notificationBuilder = NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setContentIntent(pendingIntent)
        .setChannelId("droidindonesia_pencari_beasiswa")

    notificationManager.notify(0, notificationBuilder.build())
  }

  private fun createNotificationChannel(notificationManager: NotificationManager) {
    // Channel details
    val channelId = "droidindonesia_pencari_beasiswa"
    val channelName = "Pencari Beasiswa"

    // Channel importance (3 means default importance)
    val channelImportance = 3

    try {
      // Get NotificationChannel class via reflection (only available on devices running Android O or newer)
      val notificationChannelClass = Class.forName("android.app.NotificationChannel")

      // Get NotificationChannel constructor
      val notificationChannelConstructor = notificationChannelClass.getDeclaredConstructor(String::class.java, CharSequence::class.java, Int::class.javaPrimitiveType)

      // Instantiate new notification channel
      val notificationChannel = notificationChannelConstructor.newInstance(channelId, channelName, channelImportance)

      // Get notification channel creation method via reflection
      val createNotificationChannelMethod = notificationManager.javaClass.getDeclaredMethod("createNotificationChannel", notificationChannelClass)

      // Invoke method on NotificationManager, passing in the channel object
      createNotificationChannelMethod.invoke(notificationManager, notificationChannel)

      // Log success to console
      Log.d("MyApp", "Notification channel created successfully")
    } catch (exc: Exception) {
      // Log exception to console
      Log.e("MyApp", "Creating notification channel failed", exc)
    }

  }
}