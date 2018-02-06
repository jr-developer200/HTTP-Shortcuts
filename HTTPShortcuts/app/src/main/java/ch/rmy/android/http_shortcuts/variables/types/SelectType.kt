package ch.rmy.android.http_shortcuts.variables.types

import android.content.Context
import ch.rmy.android.http_shortcuts.realm.Controller
import ch.rmy.android.http_shortcuts.realm.models.Variable
import ch.rmy.android.http_shortcuts.utils.mapFor
import org.jdeferred.Deferred

internal class SelectType : BaseVariableType(), AsyncVariableType {

    override val hasTitle = true

    override fun createDialog(context: Context, controller: Controller, variable: Variable, deferredValue: Deferred<String, Unit, Unit>): () -> Unit {
        val builder = BaseVariableType.createDialogBuilder(context, variable, deferredValue)
                .mapFor(variable.options!!) { builder, option ->
                    builder.item(option.label) {
                        deferredValue.resolve(option.value)
                        controller.setVariableValue(variable, option.value)
                    }
                }
        return {
            builder.show()
        }
    }

    override fun createEditorFragment() = SelectEditorFragment()

}
