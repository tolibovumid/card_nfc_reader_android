package uz.fozilbekimomov.nfc_scan_card

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.fozilbekimomov.nfc_scan_card.databinding.ActivityMainBinding
import uz.fozilbekimomov.nfcreader.SimpleCardReader
import uz.fozilbekimomov.nfcreader.model.EmvCard

class MainActivity : AppCompatActivity(), SimpleCardReader.SimpleCardReaderCallback,
    NfcAdapter.ReaderCallback {

    lateinit var binding: ActivityMainBinding

    //NFC
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNFC()

        binding.startNfc.setOnClickListener {

            if (requestNFC()) {
                startNFC()
            }


        }
        binding.stopNfc.setOnClickListener {
            stopNFC()
            binding.nfcData.text = "Hello world"
        }

    }

    private fun requestNFC(): Boolean {
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC on this device", Toast.LENGTH_LONG).show()
            return false
        } else if (nfcAdapter?.isEnabled == false) {

            // NFC is available for device but not enabled
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startActivityForResult(Intent(Settings.Panel.ACTION_NFC), 2265)
                } catch (ignored: ActivityNotFoundException) {
                }

            } else {
                try {
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                } catch (ignored: ActivityNotFoundException) {
                }
            }
            return false
        }
        return true
    }

    private fun stopNFC() {
        nfcAdapter?.disableReaderMode(this)
    }

    private fun startNFC() {
        nfcAdapter?.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    private fun initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun cardIsReadyToRead(card: EmvCard) {
        Log.d("LLLL", "cardIsReadyToRead: $card")
        sendData("${card.cardNumber}", "${card.expireDateMonth}/${card.expireDateYear}")
    }

    override fun cardMovedTooFastOrLockedNfc() {
        Toast.makeText(this, "Tap again", Toast.LENGTH_LONG).show()
    }

    override fun errorReadingOrUnsupportedCard() {
        Toast.makeText(this, "Error / Unsupported", Toast.LENGTH_LONG).show()
    }

    override fun onTagDiscovered(tag: Tag?) {
        SimpleCardReader.readCard(tag, this)
    }

    @SuppressLint("SetTextI18n")
    private fun sendData(number: String, date: String) {

        binding.nfcData.text = "$number\n$date"

    }
}