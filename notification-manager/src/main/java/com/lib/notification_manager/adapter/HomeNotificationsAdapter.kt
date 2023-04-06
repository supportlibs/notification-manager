package com.lib.notification_manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lib.notification_manager.R
import com.lib.notification_manager.data.NotificationModelWrapper

class HomeNotificationsAdapter : RecyclerView.Adapter<HomeNotificationsAdapter.ViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<NotificationModelWrapper>() {
        override fun areItemsTheSame(oldItem: NotificationModelWrapper, newItem: NotificationModelWrapper) = oldItem == newItem
        override fun areContentsTheSame(oldItem: NotificationModelWrapper, newItem: NotificationModelWrapper) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, callback)

    private val notificationItemList: List<NotificationModelWrapper>
        get() = differ.currentList

    fun submitList(list: List<NotificationModelWrapper>) {
        differ.submitList(list)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_app, parent, false)
        )

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(notificationItemList[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivApp: ImageView = itemView.findViewById(R.id.iv_app)
        private val notificationsCount: TextView = itemView.findViewById(R.id.notifications_count)
        fun onBind(app: NotificationModelWrapper) {
            ivApp.setImageDrawable(app.notificationModel.icon)
            notificationsCount.isVisible = app.count > 0
            notificationsCount.text = app.count.toString()
        }
    }
}