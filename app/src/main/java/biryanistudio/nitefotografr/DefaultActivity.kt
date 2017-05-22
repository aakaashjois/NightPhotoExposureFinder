package biryanistudio.nitefotografr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.biryanistudio.nightphotoexposurefinder.R
import kotlinx.android.synthetic.main.activity_default.*

class DefaultActivity : AppCompatActivity(), View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    var focalLength: Int = 0
    var sensorSize: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        manufacturerHelp.setOnClickListener(this)
        sensorSizeHelp.setOnClickListener(this)
        focalLengthHelp.setOnClickListener(this)
        shutterSpeedHelp.setOnClickListener(this)
        manufacturerList.setOnCheckedChangeListener(this)
        sensorSizeList.setOnCheckedChangeListener(this)
        focalLengthPicker.setOnValueChangedListener(this)
        focalLengthPicker.minValue = 0
        focalLengthPicker.maxValue = 300
    }

    override fun onClick(v: View?) {
        when (v) {
            manufacturerHelp -> when (manufacturerDescription.visibility) {
                View.GONE -> changeDescriptionState(manufacturerHelp, manufacturerDescription, false)
                View.VISIBLE -> changeDescriptionState(manufacturerHelp, manufacturerDescription, true)
            }
            sensorSizeHelp -> when (sensorSizeDescription.visibility) {
                View.GONE -> changeDescriptionState(sensorSizeHelp, sensorSizeDescription, false)
                View.VISIBLE -> changeDescriptionState(sensorSizeHelp, sensorSizeDescription, true)
            }
            focalLengthHelp -> when (focalLengthDescription.visibility) {
                View.GONE -> changeDescriptionState(focalLengthHelp, focalLengthDescription, false)
                View.VISIBLE -> changeDescriptionState(focalLengthHelp, focalLengthDescription, true)
            }
            shutterSpeedHelp -> when (shutterSpeedDescription.visibility) {
                View.GONE -> changeDescriptionState(shutterSpeedHelp, shutterSpeedDescription, false)
                View.VISIBLE -> changeDescriptionState(shutterSpeedHelp, shutterSpeedDescription, true)
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group) {
            manufacturerList -> {
                sensorSizeList.removeAllViews()
                val selectedRadioButton = group?.getChildAt(checkedId - 1) as RadioButton
                getAppropriateSensorSizeArray(selectedRadioButton.text as String)?.forEachIndexed { index, s ->
                    val radioButton = layoutInflater.inflate(R.layout.radio_button_view,
                            sensorSizeList, false) as RadioButton
                    radioButton.text = s
                    radioButton.id = index
                    sensorSizeList.addView(radioButton)
                }
                calculateShutterSpeed()
            }
            sensorSizeList -> {
                val selectedRadioButton = group?.findViewById(checkedId) as RadioButton
                sensorSize = (selectedRadioButton.text as String).toDouble()
                calculateShutterSpeed()
            }
        }
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        focalLength = newVal
        calculateShutterSpeed()
    }

    /**
     * Shows or hides the Help section of the card based on @param hide
     */
    fun changeDescriptionState(viewHelp: TextView, viewDescription: TextView, hide: Boolean) {
        val visibility: Int
        val drawable: Int
        if (hide) {
            visibility = View.GONE
            drawable = R.drawable.expand_more
        } else {
            visibility = View.VISIBLE
            drawable = R.drawable.expand_less
        }
        viewDescription.visibility = visibility
        viewHelp.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
    }

    /**
     * Returns the correct sensor size array based on Manufacturer chosen
     */
    fun getAppropriateSensorSizeArray(index: String): Array<String>? {
        var arrayIndex: Int = -1
        when (index) {
            getString(R.string.canon) -> arrayIndex = R.array.canonCrop
            getString(R.string.fujifilm) -> arrayIndex = R.array.fujifilmCrop
            getString(R.string.minolta) -> arrayIndex = R.array.minoltaCrop
            getString(R.string.leica) -> arrayIndex = R.array.leicaCrop
            getString(R.string.nikon) -> arrayIndex = R.array.nikonCrop
            getString(R.string.pentax) -> arrayIndex = R.array.pentaxCrop
            getString(R.string.samsung) -> arrayIndex = R.array.samsungCrop
            getString(R.string.sigma) -> arrayIndex = R.array.sigmaCrop
            getString(R.string.sony) -> arrayIndex = R.array.sonyCrop
        }
        when (arrayIndex) {
            -1 -> return null
            else -> return resources.getStringArray(arrayIndex)
        }
    }

    /**
     * Calculates and displaying the shutter speed
     */
    fun calculateShutterSpeed() {
        if (focalLength != 0 && sensorSize != 0.0) {
            val shutterSpeed = (500 / (focalLength * sensorSize)).toInt()
            stillShutterSpeed.text = SpannableStringBuilder("Lesser than $shutterSpeed\u0022")
            startTrailShutterSpeed.text = SpannableStringBuilder("Greater than $shutterSpeed\u0022")
        }
    }
}