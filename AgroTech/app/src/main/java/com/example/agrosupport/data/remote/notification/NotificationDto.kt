package com.example.agrosupport.data.remote.notification

import com.example.agrosupport.domain.notification.Notification

data class NotificationDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val message: String,
    val sendAt: String
)

fun NotificationDto.toNotification() = Notification(id, userId, title, message, sendAt)