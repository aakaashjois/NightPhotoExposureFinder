package biryanistudio.nitefotografr;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.spinnerManufacturer) Spinner spinnerManufacturer;
    @Bind(R.id.radioSensorType) RadioGroup radioSensorType;
    @Bind(R.id.spinnerSensorType) Spinner spinnerSensorType;
    @Bind(R.id.textFocalLength) EditText textFocalLength;
    @Bind(R.id.textTrail) TextView textTrail;
    @Bind(R.id.textStill) TextView textStill;

    int chosenManufacturer = -1;
    double sensorTypeValue = -1.0;

    String[] arraySensorCrop = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeUI();
    }

    private void initializeUI() {
        spinnerSensorType.setVisibility(View.GONE);
        showManufacturerCard();
        getCropType();
        getFocalLength();
        animateAllCards();
    }

    private void animateAllCards() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int height = p.y;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        for(int i = 0;i<linearLayout.getChildCount();i++) {
            View toAnimateView = linearLayout.getChildAt(i);
            float originalYPos = toAnimateView.getY();
            toAnimateView.setTranslationY(height);
            toAnimateView.animate().translationYBy(originalYPos-height)
                    .setInterpolator(interpolator).setStartDelay(i*125).setDuration(500).start();
        }
    }

    private void showManufacturerCard() {
        String[] arrayManufacturer = getResources().getStringArray(R.array.manufacturerList);
        ArrayAdapter<String> adapterManufacturer = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arrayManufacturer);
        spinnerManufacturer.setAdapter(adapterManufacturer);
        spinnerManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenManufacturer = position;
                switch (chosenManufacturer) {
                    case 2:
                    case 3:
                    case 7:
                    case 9:
                        spinnerSensorType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void getCropType() {
        radioSensorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioFullFrame) {
                    sensorTypeValue = 1.0;
                } else if (checkedId == R.id.radioCropFrame) {
                    switch (chosenManufacturer) {
                        case 2:
                        case 3:
                        case 7:
                        case 9:
                            sensorTypeValue = 1.5;
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
        spinnerSensorType.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapterSensorCrop = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arraySensorCrop);
        spinnerSensorType.setAdapter(adapterSensorCrop);
        spinnerSensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) sensorTypeValue = Double.parseDouble(arraySensorCrop[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getFocalLength() {
        textFocalLength.setEnabled(true);
        textFocalLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String inputText = textFocalLength.getText().toString();
                    if(!inputText.isEmpty()) {
                        double inputFocalLength = Double.parseDouble(inputText);
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(textFocalLength.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        getShutterSpeed(inputFocalLength);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getShutterSpeed(double inputFocalLength) {
        try {
            double shutterSpeed = 500 / (inputFocalLength * sensorTypeValue);
            String trail = getResources().getString(R.string.trail);
            trail = trail + (String.valueOf(((int) shutterSpeed + 1)));
            trail = trail + getResources().getString(R.string.seconds);
            textTrail.setText(trail);
            String still = getResources().getString(R.string.still);
            still = still + (String.valueOf(((int) shutterSpeed - 1)));
            still = still + getResources().getString(R.string.seconds);
            textStill.setText(still);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
