package ch.rmy.android.http_shortcuts.actions.types

import android.content.Context
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.actions.ActionDTO

@Deprecated("Will be removed eventually")
class ExtractHeaderActionType(context: Context) : BaseActionType(context) {

    override val type = TYPE

    override val title: String = context.getString(R.string.action_type_extract_header_title)

    override fun fromDTO(actionDTO: ActionDTO) = ExtractHeaderAction(this, actionDTO.data)

    companion object {

        const val TYPE = "extract_header"

    }

}