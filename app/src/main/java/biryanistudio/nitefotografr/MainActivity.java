package biryanistudio.nitefotografr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.spinnerManufacturer) Spinner spinnerManufacturer;
    @Bind(R.id.radioSensorType) RadioGroup radioSensorType;
    @Bind(R.id.radioFullFrame) RadioButton radioFullFrame;
    @Bind(R.id.radioCropFrame) RadioButton radioCropFrame;
    @Bind(R.id.spinnerSensorType) Spinner spinnerSensorType;
    @Bind(R.id.textFocalLength) EditText textFocalLength;
    @Bind(R.id.textTrail) TextView textTrail;
    @Bind(R.id.textStill) TextView textStill;
    @Bind(R.id.linearLayout) ViewGroup root;

    int chosenManufacturer = -1;
    int chosenCropType = -1;
    double sensorTypeValue = -1.0;
    double inputFocalLength = -1.0;
    double shutterSpeed = -1.0;

    String[] arrayManufacturer = new String[]{};
    String[] arraySensorCrop = new String[]{};

    CardAnimator cardAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupFAB();
        cardAnimator = new CardAnimator(this, root);
        cardAnimator.hideAllCards();
        getManufacturer();

    }

    private void setupFAB() {
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
    }

    private void getManufacturer() {
        cardAnimator.animateNextViewInFromLeft();

        arrayManufacturer = getResources().getStringArray(R.array.manufacturerList);
        ArrayAdapter<String> adapterManufacturer = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arrayManufacturer);
        spinnerManufacturer.setAdapter(adapterManufacturer);
        spinnerManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenManufacturer = position;
                if (position != 0) {
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
        cardAnimator.animateNextViewInFromLeft();
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
        spinnerSensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    chosenCropType = position;
                    sensorTypeValue = Double.parseDouble(arraySensorCrop[position]);
                    spinnerSensorType.setSelection(position);
                    radioCropFrame.setEnabled(false);
                    radioFullFrame.setEnabled(false);
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
        cardAnimator.animateNextViewInFromLeft();

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
        cardAnimator.animateNextViewInFromLeft();

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
        cardAnimator.resetAllViews();

        chosenManufacturer = -1;
        sensorTypeValue = -1.0;
        chosenCropType = -1;
        shutterSpeed = -1.0;
        inputFocalLength = -1.0;
        arrayManufacturer = new String[]{};
        arraySensorCrop = new String[]{};

        radioCropFrame.setEnabled(true);
        radioFullFrame.setEnabled(true);
        radioSensorType.clearCheck();

        textStill.setText(null);
        textTrail.setText(null);
        textFocalLength.setText(null);
    }

}
