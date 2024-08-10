package com.example.simongame.intent

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class ShareResult() : ActivityResultContract<Int, Void?>() {
    override fun createIntent(context: Context, input: Int): Intent =
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "I just made a score of $input in Super Simon! I challenge you to beat me!")
            type = "text/plain"
        }

    // No result
    override fun parseResult(resultCode: Int, intent: Intent?): Void? = null
}