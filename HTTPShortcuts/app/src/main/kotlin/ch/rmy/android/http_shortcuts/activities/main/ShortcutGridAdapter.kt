package ch.rmy.android.http_shortcuts.activities.main

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.activities.BaseViewHolder
import ch.rmy.android.http_shortcuts.data.livedata.ListLiveData
import ch.rmy.android.http_shortcuts.data.models.Shortcut
import ch.rmy.android.http_shortcuts.icons.IconView
import kotterknife.bindView

class ShortcutGridAdapter(context: Context, shortcuts: ListLiveData<Shortcut>) : BaseShortcutAdapter(context, shortcuts) {

    override fun createViewHolder(parentView: ViewGroup) = ShortcutViewHolder(parentView)

    inner class ShortcutViewHolder(parent: ViewGroup) : BaseViewHolder<Shortcut>(parent, R.layout.grid_item_shortcut, this@ShortcutGridAdapter) {

        private val name: TextView by bindView(R.id.name)
        private val icon: IconView by bindView(R.id.icon)

        override fun updateViews(item: Shortcut) {
            name.text = item.name
            icon.setIcon(item.iconName)
            name.setTextColor(nameTextColor)
        }

    }

}
