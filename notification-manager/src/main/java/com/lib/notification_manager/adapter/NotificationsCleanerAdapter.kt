package com.lib.notification_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lib.notification_manager.R
import com.lib.notification_manager.data.NotificationModel

class NotificationsCleanerAdapter(
    private var notificationItemList: List<NotificationsCleanerItem>,
    private val onItemClick: ((item: NotificationsCleanerItem, checked: Boolean) -> Unit)
) : RecyclerView.Adapter<NotificationsCleanerAdapter.ViewHolder>() {

    data class NotificationsCleanerItem(
        val notification: NotificationModel,
        var isChecked: Boolean = false
    )

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.app_icon)
        var title: TextView = itemView.findViewById(R.id.app_title)
        var message: TextView = itemView.findViewById(R.id.notification_message)
        var checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(item: NotificationsCleanerItem) {
            icon.setImageDrawable(item.notification.icon)
            title.text = item.notification.applicationName
            message.text = item.notification.text
            checkbox.isChecked = item.isChecked
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                notificationItemList[adapterPosition].isChecked = isChecked
                onItemClick.invoke(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_notification_for_clean, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationItem = notificationItemList[position]
        holder.bind(notificationItem)
    }

    override fun getItemCount() = notificationItemList.size

    fun chooseAll(check: Boolean) {
        notificationItemList.forEach { item ->
            item.isChecked = check
        }
        notifyItemRangeChanged(0, itemCount)
    }

    fun getCheckedNotificationsKeys(): Array<String> {
        return notificationItemList.filter { it.isChecked }.map { it.notification.key }.toTypedArray()
    }

    fun setNotificationList(newNotificationItemList: List<NotificationsCleanerItem>) {
        notificationItemList = newNotificationItemList
        notifyDataSetChanged()
    }

    fun countCheckedNotifications() = notificationItemList.count { it.isChecked }
}