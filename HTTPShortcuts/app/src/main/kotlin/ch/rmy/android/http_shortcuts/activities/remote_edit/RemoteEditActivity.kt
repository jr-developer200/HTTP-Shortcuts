package ch.rmy.android.http_shortcuts.activities.remote_edit

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.activities.BaseActivity
import ch.rmy.android.http_shortcuts.import_export.Exporter
import ch.rmy.android.http_shortcuts.import_export.Importer
import ch.rmy.android.http_shortcuts.utils.BaseIntentBuilder
import ch.rmy.android.http_shortcuts.utils.NetworkUtil
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotterknife.bindView
import java.io.InputStream

class RemoteEditActivity : BaseActivity() {

    private lateinit var server: ApplicationEngine

    private val ipAddressView: TextView by bindView(R.id.ip_address)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_edit)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        server = createServer()

        // TODO: Display instructions (including IP address)
        // TODO: Subscribe IntentFilter to observe network and IP address changes
        // TODO: Display a log of incoming connections and performed actions
    }

    private fun createServer(): ApplicationEngine =
        embeddedServer(Netty, SERVER_PORT) {
            install(CORS) {
                anyHost()
            }
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }
            routing {
                get("/base") {
                    call.respondTextWriter(ContentType.Application.Json) {
                        export(this)
                    }
                }
                post("/base") {
                    val importStatus = import(call.request.receiveChannel().toInputStream())
                    call.respond(mapOf(
                        "status" to "success",
                        "updatedShortcuts" to importStatus.importedShortcuts,
                    ))
                }
            }
        }

    fun updateIPAddressView() {
        ipAddressView.text = NetworkUtil.getIPV4Address(context) ?: "-"
    }

    fun export(writer: Appendable) {
        Exporter(context).export(writer)
    }

    fun import(inputStream: InputStream) =
        Importer(context).import(inputStream)

    override fun onStart() {
        super.onStart()
        server.start(wait = false)
        updateIPAddressView()
    }

    override fun onStop() {
        super.onStop()
        server.stop(1000, 1000)
    }

    class IntentBuilder(context: Context) : BaseIntentBuilder(context, RemoteEditActivity::class.java)

    companion object {

        private const val SERVER_PORT = 9274

    }

}