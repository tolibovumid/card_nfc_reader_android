#### If you enjoy my content, please consider supporting what I do. Thank you.

[![](https://user-images.githubusercontent.com/36783954/183887369-a0565898-0ed7-4049-877a-c688503aad90.png)](https://www.buymeacoffee.com/fozilbekimomov)

[By me a Coffee](https://www.buymeacoffee.com/fozilbekimomov)

***
[![](https://jitpack.io/v/FozilbekImomov/card_nfc_reader_android.svg)](https://jitpack.io/#FozilbekImomov/card_nfc_reader_android)




To get a Git project into your build:

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
  
allprojects {
   repositories {
     ..
     maven { url 'https://jitpack.io'}
     ..
  }
}

```

### Step 2. Add the dependency

Gradle:

```gradle
//NFC
implementation 'com.github.FozilbekImomov:card_nfc_reader_android:1.0.3'
//Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:latest-version'
```
Maven:

```gradle
<dependency>
  <groupId>uz.fozilbekimomov.lite_lib</groupId>
  <artifactId>card_nfc_reader_android</artifactId>
  <version>1.0.3</version>
</dependency>

<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.6.4</version>
</dependency>

```

### Step 3. Add NFC permission in your project AndroidManifest.xml

```gradle

 <uses-permission android:name="android.permission.NFC" />

```

### Step 4. Modify your App Activity

```kotlin


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

```
