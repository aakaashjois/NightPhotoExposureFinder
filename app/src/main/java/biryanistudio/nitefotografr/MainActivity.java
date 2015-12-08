package biryanistudio.nitefotografr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CardView cardManufacturer;
    Spinner spinnerManufacturer;

    CardView cardSensorType;
    RadioGroup radioSensorType;
    RadioButton radioFullFrame;
    RadioButton radioCropFrame;
    Spinner spinnerSensorType;

    CardView cardFocalLength;
    EditText textFocalLength;

    CardView cardShutterSpeed;
    TextView textTrail;
    TextView textStill;

    int chosenManufacturer = -1;
    int chosenCropType = -1;
    double sensorTypeValue = -1.0;
    double inputFocalLength = -1.0;
    double shutterSpeed = -1.0;

    boolean isFirstSelectManufacturer = true;
    boolean isFirstSelectCropType = true;

    String[] arrayManufacturer = new String[]{};
    String[] arraySensorCrop = new String[]{};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Reset everything?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                resetAll();
                            }
                        })
                        .show();
            }
        });

        cardManufacturer = (CardView) findViewById(R.id.cardManufacturer);
        spinnerManufacturer = (Spinner) findViewById(R.id.spinnerManufacturer);

        cardSensorType = (CardView) findViewById(R.id.cardSensorType);
        radioSensorType = (RadioGroup) findViewById(R.id.radioSensorType);
        radioFullFrame = (RadioButton) findViewById(R.id.radioFullFrame);
        radioCropFrame = (RadioButton) findViewById(R.id.radioCropFrame);
        spinnerSensorType = (Spinner) findViewById(R.id.spinnerSensorType);

        cardFocalLength = (CardView) findViewById(R.id.cardFocalLength);
        textFocalLength = (EditText) findViewById(R.id.textFocalLength);

        cardShutterSpeed = (CardView) findViewById(R.id.cardShutterSpeed);
        textTrail = (TextView) findViewById(R.id.textTrail);
        textStill = (TextView) findViewById(R.id.textStill);

        cardManufacturer.setVisibility(View.GONE);
        cardSensorType.setVisibility(View.GONE);
        cardFocalLength.setVisibility(View.GONE);
        cardShutterSpeed.setVisibility(View.GONE);

        getManufacturer();

    }

    private void getManufacturer() {
        cardManufacturer.setVisibility(View.VISIBLE);
        arrayManufacturer = getResources().getStringArray(R.array.manufacturerList);
        ArrayAdapter<String> adapterManufacturer = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arrayManufacturer);
        spinnerManufacturer.setAdapter(adapterManufacturer);
        spinnerManufacturer.setEnabled(true);
        spinnerManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenManufacturer = position;
                if (chosenManufacturer == 0) {
                    if (isFirstSelectManufacturer) {
                        isFirstSelectManufacturer = !isFirstSelectManufacturer;
                    } else
                        Snackbar.make(view, "Please choose your camera Manufacturer", Snackbar.LENGTH_LONG).show();
                } else {
                    spinnerManufacturer.setSelection(position);
                    spinnerManufacturer.setEnabled(false);
                    getCropType();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void getCropType() {
        cardSensorType.setVisibility(View.VISIBLE);
        spinnerSensorType.setEnabled(false);
        radioSensorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioFullFrame) {
                    sensorTypeValue = 1.0;
                    radioFullFrame.setEnabled(false);
                    radioCropFrame.setEnabled(false);
                    getFocalLength();
                } else if (checkedId == R.id.radioCropFrame) {
                    switch (chosenManufacturer) {

                        case 2:
                        case 3:
                        case 7:
                        case 9:
                            sensorTypeValue = 1.5;
                            radioFullFrame.setEnabled(false);
                            radioCropFrame.setEnabled(false);
                            getFocalLength();
                            break;
                        case 1:
                            arraySensorCrop = getResources().getStringArray(R.array.canonCrop);
                            getCropFactor();
                            break;
                        case 4:
                            arraySensorCrop = getResources().getStringArray(R.array.leicaCrop);
                            getCropFactor();
                            break;
                        case 5:
                            arraySensorCrop = getResources().getStringArray(R.array.nikonCrop);
                            getCropFactor();
                            break;
                        case 6:
                            arraySensorCrop = getResources().getStringArray(R.array.pentaxCrop);
                            getCropFactor();
                            break;
                        case 8:
                            arraySensorCrop = getResources().getStringArray(R.array.sigmaCrop);
                            getCropFactor();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    private void getCropFactor() {

        ArrayAdapter<String> adapterSensorCrop = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arraySensorCrop);
        spinnerSensorType.setAdapter(adapterSensorCrop);
        spinnerSensorType.setEnabled(true);
        spinnerSensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isFirstSelectCropType) {
                    isFirstSelectCropType = !isFirstSelectCropType;
                } else {
                    chosenCropType = position;
                    sensorTypeValue = Double.parseDouble(arraySensorCrop[position]);
                    spinnerSensorType.setSelection(position);
                    spinnerSensorType.setEnabled(false);
                    getFocalLength();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getFocalLength() {

        cardFocalLength.setVisibility(View.VISIBLE);
        textFocalLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    inputFocalLength = Double.parseDouble(textFocalLength.getText().toString());
                    textFocalLength.setEnabled(false);
                    getShutterSpeed();
                    return true;
                }
                return false;
            }
        });
    }

    private void getShutterSpeed() {

        cardShutterSpeed.setVisibility(View.VISIBLE);
        shutterSpeed = 500 / (inputFocalLength * sensorTypeValue);
        String trail = getResources().getString(R.string.trail);
        trail = trail + (String.valueOf(((int) shutterSpeed + 1)));
        trail = trail + getResources().getString(R.string.seconds);
        textTrail.setText(trail);
        String still = getResources().getString(R.string.still);
        still = still + (String.valueOf(((int) shutterSpeed - 1)));
        still = still + getResources().getString(R.string.seconds);
        textStill.setText(still);

    }

    private void resetAll() {

        cardShutterSpeed.setVisibility(View.GONE);
        cardFocalLength.setVisibility(View.GONE);
        cardSensorType.setVisibility(View.GONE);
        cardManufacturer.setVisibility(View.GONE);

        isFirstSelectManufacturer = true;
        chosenManufacturer = -1;
        arrayManufacturer = new String[]{};

        isFirstSelectCropType = true;
        radioCropFrame.setEnabled(true);
        radioFullFrame.setEnabled(true);
        radioSensorType.clearCheck();
        arraySensorCrop = new String[]{};
        sensorTypeValue = -1.0;
        chosenCropType = -1;

        textStill.setText(null);
        textTrail.setText(null);
        shutterSpeed = -1.0;

        inputFocalLength = -1.0;
        textFocalLength.setEnabled(true);
        textFocalLength.setText(null);

        getManufacturer();
    }

}
