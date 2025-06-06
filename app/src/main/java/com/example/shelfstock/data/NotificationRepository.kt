// NotificationRepository.kt
package com.example.shelfstock.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class NotificationRepository(private val notificationDao: NotificationDao) {

    val allNotifications: Flow<List<Notification>> = notificationDao.getAllNotifications()
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }

    val unreadNotifications: Flow<List<Notification>> = notificationDao.getUnreadNotifications()
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }

    suspend fun insertNotification(notification: Notification): Result<Unit> = try {
        notificationDao.insertNotification(notification)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun markAsRead(id: Int): Result<Unit> = try {
        notificationDao.markAsRead(id)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun markAllAsRead(): Result<Unit> = try {
        notificationDao.markAllAsRead()
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun deleteAllNotifications(): Result<Unit> = try {
        notificationDao.deleteAllNotifications()
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}