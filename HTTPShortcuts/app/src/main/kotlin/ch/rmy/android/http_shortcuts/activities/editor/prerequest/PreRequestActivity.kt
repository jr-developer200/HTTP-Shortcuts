package ch.rmy.android.http_shortcuts.activities.editor.prerequest

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.widget.EditText
import androidx.lifecycle.Observer
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.actions.ActionDTO
import ch.rmy.android.http_shortcuts.actions.ActionsUtil
import ch.rmy.android.http_shortcuts.actions.types.ActionFactory
import ch.rmy.android.http_shortcuts.actions.types.BaseAction
import ch.rmy.android.http_shortcuts.activities.BaseActivity
import ch.rmy.android.http_shortcuts.extensions.attachTo
import ch.rmy.android.http_shortcuts.extensions.bindViewModel
import ch.rmy.android.http_shortcuts.extensions.observeTextChanges
import ch.rmy.android.http_shortcuts.extensions.setTextSafely
import ch.rmy.android.http_shortcuts.utils.BaseIntentBuilder
import ch.rmy.android.http_shortcuts.variables.VariablePlaceholderProvider
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotterknife.bindView
import java.util.concurrent.TimeUnit

class PreRequestActivity : BaseActivity() {

    private val viewModel: PreRequestViewModel by bindViewModel()
    private val shortcutData by lazy {
        viewModel.shortcut
    }
    private val variablesData by lazy {
        viewModel.variables
    }
    private val variablePlaceholderProvider by lazy {
        VariablePlaceholderProvider(variablesData)
    }
    private val actionFactory by lazy {
        ActionFactory(context)
    }

    private val prepareCodeInput: EditText by bindView(R.id.input_code_prepare)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_request)

        initViews()
        bindViewsToViewModel()
    }

    private fun initViews() {
        prepareCodeInput.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindViewsToViewModel() {
        shortcutData.observe(this, Observer {
            updateShortcutViews()
        })
        bindTextChangeListener(prepareCodeInput) { shortcutData.value?.codeOnPrepare }
    }

    private fun bindTextChangeListener(textView: EditText, currentValueProvider: () -> String?) {
        textView.observeTextChanges()
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                ActionsUtil.removeSpans(it as Spannable)
            }
            .filter { it.toString() != currentValueProvider.invoke() }
            .concatMapCompletable { updateViewModelFromViews() }
            .subscribe()
            .attachTo(destroyer)
    }

    private fun updateViewModelFromViews(): Completable =
        viewModel.setCodeOnPrepare(
            code = ActionsUtil.removeSpans(prepareCodeInput.text)
        )

    private fun updateShortcutViews() {
        val shortcut = shortcutData.value ?: return
        prepareCodeInput.setTextSafely(
            ActionsUtil.addSpans(
                context,
                shortcut.codeOnPrepare,
                actionFactory,
                ::editAction
            )
        )
    }

    private fun editAction(action: BaseAction, setter: (ActionDTO) -> Unit) {
        action.edit(context, variablePlaceholderProvider)
            .subscribe(
                {
                    setter(action.toDTO())
                    updateViewModelFromViews()
                        .subscribe()
                        .attachTo(destroyer)
                },
                {}
            )
            .attachTo(destroyer)
    }

    class IntentBuilder(context: Context) : BaseIntentBuilder(context, PreRequestActivity::class.java)

}